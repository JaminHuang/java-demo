package com.demo.ibatx.spring.interceptor;

import com.demo.ibatx.plugin.InterceptorAdapter;
import com.demo.ibatx.plugin.InterceptorHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfiguration implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired(required = false)
    private List<InterceptorAdapter> interceptorAdapters;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        InterceptorHandler interceptorHandler = new InterceptorHandler();
        interceptorHandler.addInterceptorAdapter(interceptorAdapters);
        sqlSessionFactory.getConfiguration().addInterceptor(interceptorHandler);
    }
}
