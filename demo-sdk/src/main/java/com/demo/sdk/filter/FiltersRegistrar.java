package com.demo.sdk.filter;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * 过滤器注册
 */
@Import({RequestLogFilter.class, ThreadLocalFilter.class, WebSwitchFilter.class})
public class FiltersRegistrar {

    @Bean
    public FilterRegistrationBean requestLogFilterRegistration(RequestLogFilter requestLogFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(requestLogFilter);
        registration.addUrlPatterns("/*");
        registration.setName("requestLogFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 8);
        return registration;
    }

    @Bean
    public FilterRegistrationBean threadLocalFilterRegistration(ThreadLocalFilter threadLocalFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(threadLocalFilter);
        registration.addUrlPatterns("/*");
        registration.setName("threadLocalFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 10);
        return registration;
    }

    @Bean
    public FilterRegistrationBean webSwitchFilterRegistration(WebSwitchFilter webSwitchFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(webSwitchFilter);
        registration.addUrlPatterns("/*");
        registration.setName("webSwitchFilter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 12);
        return registration;
    }

}