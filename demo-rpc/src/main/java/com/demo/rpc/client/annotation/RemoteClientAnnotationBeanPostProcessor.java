package com.demo.rpc.client.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * 客户端实例化
 *
 * @author caiLinFeng
 * @date 2018年12月12日
 */
@Component
public class RemoteClientAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements PriorityOrdered {

    private static Logger logger = LoggerFactory.getLogger(RemoteClientAnnotationBeanPostProcessor.class);

    private int order = PriorityOrdered.HIGHEST_PRECEDENCE;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        BeanDecorator.decorator(bean, beanName);
        return bean;
    }

    @Override
    public int getOrder() {
        return order;
    }

}
