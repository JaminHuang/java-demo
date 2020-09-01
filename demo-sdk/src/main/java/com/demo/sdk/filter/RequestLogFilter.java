package com.demo.sdk.filter;

import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * api 工程日志记录
 */
public class RequestLogFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtils redisUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startMillis = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // 获取请求参数
        String query = wrappedRequest.getQueryString();
        String body = getBody(wrappedRequest);
        ReqThreadLocal.setQuery(query);
        ReqThreadLocal.setBody(body);

        filterChain.doFilter(wrappedRequest, response);

        logTrace(wrappedRequest, startMillis);
    }

    private String getBody(ContentCachingRequestWrapper request) {
        // wrap request to make sure we can read the body of the request (otherwise it will be consumed by the actual
        // request handler)
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getBody();
            if (buf.length > 0) {
                String payload;
                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                    payload = "[unknown]";
                }
                return payload;
            }
        }
        return null;
    }

    private void logTrace(HttpServletRequest request, long startMillis) {
        Map<String, Object> trace = new LinkedHashMap<>();
        String accessToken = ReqThreadLocal.getAccessToken();
        Integer userId = ReqThreadLocal.getUserId();
        String ip = ReqThreadLocal.getIp();
        String body = ReqThreadLocal.getBody();
        String query = ReqThreadLocal.getQuery();
        String traceId = ReqThreadLocal.getTid();
        trace.put("useMillis", System.currentTimeMillis() - startMillis);
        trace.put("uri", request.getRequestURI());
        trace.put("method", request.getMethod());
        trace.put("userId", userId);
        trace.put("accessToken", accessToken);
        trace.put("ip", ip);
        trace.put("query", query);
        trace.put("body", body);
        trace.put("tid", traceId);
        logger.info("Trace: {}", trace);
    }


}
