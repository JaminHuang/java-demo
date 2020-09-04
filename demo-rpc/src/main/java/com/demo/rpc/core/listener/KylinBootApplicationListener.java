package com.demo.rpc.core.listener;

import com.demo.rpc.core.SpringContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;

/**
 *
 * 用于springboot启动时，将spring管理的部分对象放入springcontext对象中，供非spring管理类调用
 */
public class KylinBootApplicationListener implements SmartApplicationListener {

    private int order = Ordered.HIGHEST_PRECEDENCE + 10;

    private static Class<?>[] EVENT_TYPES = {ApplicationEnvironmentPreparedEvent.class};

    private static Class<?>[] SOURCE_TYPES = {SpringApplication.class,
            ApplicationContext.class};

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return isAssignableFrom(eventType, EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        for (Class<?> supportedType : supportedTypes) {
            if (supportedType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            SpringContext.setEnvironment(((ApplicationEnvironmentPreparedEvent) event).getEnvironment());
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

}