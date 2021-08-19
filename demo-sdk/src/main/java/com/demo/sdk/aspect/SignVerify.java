package com.demo.sdk.aspect;

import com.demo.sdk.annotation.Sign;
import com.demo.sdk.consts.BErrorCode;
import com.demo.sdk.consts.IsDebug;
import com.demo.sdk.exception.BizException;
import com.demo.sdk.model.InnerDTO;
import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.CollectionUtils;
import com.demo.sdk.util.DateUtils;
import com.demo.sdk.util.SignUtils;
import com.demo.sdk.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SignVerify {

    private static final Logger logger = LoggerFactory.getLogger(SignVerify.class);

    // 最小时间单位 30s
    private static final long MAX_REQUEST = 30 * 1000L;

    // 签名校验列表
    private static Set<InnerDTO> innerDTOList;

    // 初始化签名校验列表
    private static void initInnerDTOList() {
        innerDTOList = new HashSet<>();
        innerDTOList.add(new InnerDTO("demo-app", "ytfbORMe8pTAAtZk", "demo客户端"));
        innerDTOList.add(new InnerDTO("demo-service", "v9VRBVWBN67NpMZs", "demo服务端"));
    }

    static void verify(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 默认不校验签名
        boolean signRequired = false;

        Class<?> clazz = method.getDeclaringClass();
        Sign signAnnotation = null;
        // 先获取类注解
        if (clazz.isAnnotationPresent(Sign.class)) {
            signAnnotation = clazz.getAnnotation(Sign.class);
            signRequired = signAnnotation.required();
        }

        // 再获取方法注解
        if (method.isAnnotationPresent(Sign.class)) {
            signAnnotation = method.getAnnotation(Sign.class);
            signRequired = signAnnotation.required();
        }

        if (!signRequired) {
            return;
        }

        // debug模式下,不校验签名
        Byte debug = ReqThreadLocal.getDebug();
        if (Objects.equals(debug, IsDebug.DEBUG)) {
            return;
        }

        // 初始化签名配置
        if (CollectionUtils.isEmpty(innerDTOList)) {
            initInnerDTOList();
        }

        // 获取map
        Map<String, String> innerMap = innerDTOList.stream().collect(Collectors.toMap(InnerDTO::getInnerAppId, InnerDTO::getInnerAppSecret));

        // 获取签名相关参数
        String xCaAppId = request.getHeader("x-ca-app-id");
        String xCaSecret = request.getHeader("x-ca-secret");
        String xCaTimestamp = request.getHeader("x-ca-timestamp");
        if (StringUtils.isEmpty(xCaAppId) || StringUtils.isEmpty(xCaSecret) || StringUtils.isEmpty(xCaTimestamp)) {
            logger.info("签名校验, 签名参数为空, [xCaAppId] = {}, [xCaSecret]= {}, [xCaTimestamp] = {}", xCaAppId, xCaSecret, xCaTimestamp);
            throw new BizException(BErrorCode.SIGN_ERROR);
        }

        // 校验时间戳
        long time = Long.parseLong(xCaTimestamp);
        if (DateUtils.getCurrentTime().getTime() - time > MAX_REQUEST) {
            logger.info("签名校验, 签名已失效, 请校对系统时间, [xCaTimestamp] = {}, [nowTimestamp] = {}", xCaTimestamp, DateUtils.getCurrentTime().getTime());
            throw new BizException(BErrorCode.SIGN_ERROR);
        }

        // 根据appId获取对应的密钥
        String innerAppSecret = innerMap.get(xCaAppId);
        if (StringUtils.isEmpty(innerAppSecret)) {
            logger.info("签名校验, 未找到对应的密钥配置, [appId] = {}", xCaAppId);
            throw new BizException(BErrorCode.SIGN_ERROR);
        }

        // 校验签名
        if (!Objects.equals(xCaSecret, SignUtils.getInnerSign(xCaTimestamp, xCaAppId, innerAppSecret))) {
            logger.info("签名校验, 签名错误");
            throw new BizException(BErrorCode.SIGN_ERROR);
        }
    }
}
