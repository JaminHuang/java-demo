package com.demo.sdk.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.demo.sdk.consts.RedisKeyEnum;
import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.ExceptionUtils;
import com.demo.sdk.util.RedisUtils;
import com.demo.sdk.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * api工程本地线程变量设置
 * rachel
 */
public class ThreadLocalFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Cache<String, Object> tokenCaches = CacheBuilder.newBuilder().expireAfterWrite(300, TimeUnit.SECONDS).build();

    private static final byte[] bytes = new byte[0];

    /**
     * token失效时间，默认2周
     */
    private static final Long ACCESS_TOKEN_TTL = 3600 * 24 * 14L;

    @Autowired
    RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            handleTid(request);
            handleIp(request);
            handleToken(request);
            handleDebug(request);
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            throw e;
        } finally {
            ReqThreadLocal.removeAll();
        }
    }

    private void handleIp(HttpServletRequest request) {
        // 设置请求IP
        String ip = RequestUtils.getIpAddr(request);
        ReqThreadLocal.setIp(ip);
    }

    /**
     * 设置链路id
     *
     * @param request
     */
    private void handleTid(HttpServletRequest request) {
        String tid = request.getHeader("tid");
        ReqThreadLocal.setTid(tid);
    }

    /**
     * 解析请求头token，并设置到本地线程变量
     *
     * @param request
     */
    private void handleToken(HttpServletRequest request) {
        // 获取请求头
        String accessToken = request.getHeader("access-token");
        if (accessToken == null || "".equals(accessToken)) {
            return;
        }
        Object userObj = redisUtils.get(String.format(RedisKeyEnum.ACCESS_TOKEN, accessToken));
        if (userObj != null) {
            JSONObject user = (JSONObject) JSON.toJSON(userObj);
            int userId = user.getIntValue("id");
            String userName = user.getString("cnName") != null
                    ? user.getString("cnName")
                    : user.getString("enName");
            ReqThreadLocal.setAccessToken(accessToken);
            ReqThreadLocal.setUserId(userId);
            ReqThreadLocal.setUserName(userName + "(" + userId + ")");
            ReqThreadLocal.setUser(user);

            // 在本地缓存中，不更新token过期时间，减少Redis操作
            if (tokenCaches.getIfPresent(accessToken) == null) {
                // 设置token本地缓存
                tokenCaches.put(accessToken, bytes);
                try {
                    // 刷新token过期时间
                    redisUtils.expire(String.format(RedisKeyEnum.ACCESS_TOKEN, accessToken), ACCESS_TOKEN_TTL, TimeUnit.SECONDS);
                    redisUtils.expire(String.format(RedisKeyEnum.LOGIN_SYS, userId), ACCESS_TOKEN_TTL, TimeUnit.SECONDS);
                } catch (Throwable e) {
                    logger.error(ExceptionUtils.getExceptionMsg(e));
                }
            }
        }
    }

    /**
     * 设置debug参数
     *
     * @param request
     */
    private void handleDebug(HttpServletRequest request) {
        String debug = request.getHeader("debug");
        ReqThreadLocal.setDebug(debug == null ? 0 : Integer.parseInt(debug));
    }

}
