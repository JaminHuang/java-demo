package com.demo.sdk.util;

import com.demo.sdk.exception.ServiceException;

import java.util.*;

/**
 * 签名工具
 */
public class SignUtils {

    /**
     * 参数MD5签名
     *
     * @param params
     * @param secret
     * @return
     */
    public static String getMD5Sign(Map<String, Object> params, String secret) {

        if (Objects.isNull(params) || CollectionUtils.isEmpty(params)) {
            throw new ServiceException("签名参数不能为空");
        }
        if (Objects.isNull(secret)) {
            throw new ServiceException("签名secret不能为空");
        }
        List<String> paramKeys = new ArrayList<>(params.keySet());
        Collections.sort(paramKeys);
        //校验逻辑
        StringBuilder sb = new StringBuilder();
        //拼接参数
        for (String key : paramKeys) {
            Object value = params.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        //拼接secret
        sb.append("secret=").append(secret);
        try {
            return MD5Util.getMD5Format(sb.toString().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("签名生成失败");
        }

    }

    /**
     * 生成签名
     * @param timestamp 时间戳
     * @param innerAppId 签名公钥
     * @param innerAppSecret 签名私钥
     */
    public static String getInnerSign(String timestamp, String innerAppId, String innerAppSecret) {
        return MD5Util.getMD5Format(timestamp + "&" + innerAppId + "&" + innerAppSecret);
    }
}
