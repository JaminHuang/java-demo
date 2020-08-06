package com.demo.rpc.server.remote;

import com.demo.rpc.hessian.properties.HeaderProperties;
import com.demo.sdk.thread.ReqThreadLocal;
import org.springframework.remoting.caucho.HessianExporter;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HessianServiceExporter extends HessianExporter implements HttpRequestHandler {

    public HessianServiceExporter() {
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"POST".equals(request.getMethod())) {
            throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[]{"POST"},
                    "HessianServiceExporter only supports POST requests");
        }
        response.setContentType(CONTENT_TYPE_HESSIAN);
        try {
            // 设置客户端带过来的请求头参数
            handleRequestHeader(request);
            invoke(request.getInputStream(), response.getOutputStream());
        } catch (Throwable ex) {
            throw new NestedServletException("Hessian skeleton invocation failed", ex);
        } finally {
            ReqThreadLocal.removeAll();
        }
    }

    private void handleRequestHeader(HttpServletRequest request) throws UnsupportedEncodingException {
        String accessToken = request.getHeader(HeaderProperties.ACCESS_TOKEN);
        String userId = request.getHeader(HeaderProperties.USER_ID);
        String userName = request.getHeader(HeaderProperties.USER_NAME);
        String tid = request.getHeader(HeaderProperties.TID);

        if (tid != null) {
            ReqThreadLocal.setTid(tid);
        }
        if (accessToken != null) {
            ReqThreadLocal.setAccessToken(accessToken);
        }
        if (userId != null) {
            ReqThreadLocal.setUserId(Integer.valueOf(userId));
        }
        if (userName != null) {
            try {
                userName = URLDecoder.decode(userName, "UTF-8");
                ReqThreadLocal.setUserName(userName);
            } catch (UnsupportedEncodingException e) {
                throw e;
            }
        }
    }

}
