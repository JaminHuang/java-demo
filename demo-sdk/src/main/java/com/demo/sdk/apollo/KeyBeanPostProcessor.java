package com.demo.sdk.apollo;

import com.demo.sdk.annotation.Key;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements PriorityOrdered {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object target = bean;
        if (AopUtils.isAopProxy(bean)) {
            try {
                target = ((Advised) bean).getTargetSource().getTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 是否继承keyParser
        if (target instanceof KeyParser) {
            KeyHolder.put((KeyParser) target);
        }
        //
        Set<Field> fields = getDeclaredFields(target.getClass());
        for (Field field : fields) {
            Key key = field.getAnnotation(Key.class);
            if (key == null) {
                continue;
            }
            KeyBean keyBean = new KeyBean();
            keyBean.setBean(target);
            keyBean.setField(field.getName());
            keyBean.setKey(key.value());
            KeyHolder.put(keyBean);
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return 0;
    }


    /**
     * 获取class所有成员属性
     *
     * @param
     * @author caiLinFeng
     * @date 2018年3月7日
     */
    private static Set<Field> getDeclaredFields(Class<?> clazz) {
        Set<Field> fieldSet = new HashSet<>();
        while (clazz != null) {
            fieldSet.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldSet;
    }
}
