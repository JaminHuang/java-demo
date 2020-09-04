package com.demo.rpc.hessian;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyFactory;
import com.demo.rpc.hessian.properties.HeaderProperties;
import com.demo.sdk.thread.ReqThreadLocal;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * hessian proxy 代理类
 */
public class KylinHessianProxy extends HessianProxy {

    /**
     * Protected constructor for subclassing
     */
    protected KylinHessianProxy(URL url, HessianProxyFactory factory) {
        super(url, factory);
    }

    /**
     * Protected constructor for subclassing
     */
    protected KylinHessianProxy(URL url, HessianProxyFactory factory, Class<?> type) {
        super(url, factory, type);
    }

    /**
     * 重载该方法
     */
    protected void addRequestHeaders(HessianConnection conn) {
        super.addRequestHeaders(conn);

        // 客户端加上请求头
        String accessToken = ReqThreadLocal.getAccessToken();
        Integer userId = ReqThreadLocal.getUserId();
        String userName = ReqThreadLocal.getUserName();
        String tid = ReqThreadLocal.getTid();
        String ip = ReqThreadLocal.getIp();
        if (ip != null) {
            conn.addHeader(HeaderProperties.IP, ip);
        }
        if (accessToken != null) {
            conn.addHeader(HeaderProperties.ACCESS_TOKEN, accessToken);
        }
        if (userId != null) {
            conn.addHeader(HeaderProperties.USER_ID, userId + "");
        }
        if (tid != null) {
            conn.addHeader(HeaderProperties.TID, tid);
        }
        if (userName != null) {
            try {
                conn.addHeader(HeaderProperties.USER_NAME, URLEncoder.encode(userName, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }
}