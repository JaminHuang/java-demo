package com.demo.sdk.aspect;

import com.alibaba.fastjson.JSONObject;
import com.demo.sdk.annotation.Resubmit;
import com.demo.sdk.consts.BErrorCode;
import com.demo.sdk.consts.RedisKeyEnum;
import com.demo.sdk.response.Result;
import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.RedisUtils;
import com.demo.sdk.util.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 重复提交aop
 */
@Aspect
@Order(1)
@Component
@EnableAspectJAutoProxy(exposeProxy = true)
public class ResubmitAspect<T> {

    private static final Logger logger = LoggerFactory.getLogger(ResubmitAspect.class);

    private static final String SPLIT = "#";

    private static final String DEFAULT_DESCRIPTION = "请勿重复提交";

    @Autowired
    RedisUtils redisUtils;

    /**
     * @param point
     */
    @SuppressWarnings("unchecked")
    @Around("@annotation(com.demo.sdk.annotation.Resubmit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();

        String uri = request.getRequestURI();
        // 优先从ReqThreadLocal获取
        Integer userId = ReqThreadLocal.getUserId();
        // 创建请求重复标识
        StringBuilder sb = new StringBuilder(uri);
        sb.append(SPLIT).append(userId);

        // 请求参数
        Object[] args = point.getArgs();
        if (args != null && args.length != 0) {
            for (Object obj : args) {
                if (obj instanceof HttpServletRequest || obj instanceof HttpServletResponse) {
                    continue;
                }
                sb.append(SPLIT).append(JSONObject.toJSONString(obj));
            }
        }

        String key = String.format(RedisKeyEnum.RESUBMIT, sb.toString().hashCode());

        Resubmit resubmit = method.getAnnotation(Resubmit.class);
        int timeout = resubmit.timeout();
        String description = resubmit.description();

        if (timeout < 0) {
            timeout = 3;
        }
        if (StringUtils.isEmpty(description)) {
            description = DEFAULT_DESCRIPTION;
        }

        long count = redisUtils.incrBy(key, 1);
        // 设置有效期
        if (count == 1) {
            // 有设置不成功风险
            redisUtils.expire(key, timeout);
            return point.proceed();
        }
        return Result.fail(BErrorCode.RESUBMIT, description);
    }

}
