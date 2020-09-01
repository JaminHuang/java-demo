package com.demo.sdk.filter;

import com.demo.sdk.support.WebContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * api工程web开关过滤
 */
public class WebSwitchFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!WebContainer.isRuning()) {
            // 网关层处理重试
            logger.info("应用关闭中...");
            response.addHeader("Server-Stop", "true");
            return;
        }
        try {
            WebContainer.incrementAndGet();
            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            throw e;
        } finally {
            WebContainer.decrementAndGet();
        }
    }
}
