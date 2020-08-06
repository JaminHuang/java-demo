package com.demo.rpc.client.annotation;

import com.demo.rpc.annotation.Remote;
import com.demo.rpc.annotation.RemoteResource;
import com.demo.rpc.client.utils.ProxyBuilder;
import com.demo.rpc.core.SpringContext;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 给属性设置代理类
 */
public class BeanDecorator {

    public static void decorator(Object bean, String beanName) {
        Field[] fields = bean.getClass().getSuperclass().getDeclaredFields();
        if (!ClassUtils.isCglibProxy(bean) && !Proxy.isProxyClass(bean.getClass())) {
            fields = bean.getClass().getDeclaredFields();
        }
        for (Field field : fields) {
            RemoteResource client = field.getDeclaredAnnotation(RemoteResource.class);
            if (client != null) {

                String url = client.url();
                long connectTimeout = client.connectTimeout();
                long readTimeout = client.readTimeout();
                Class<?> interfaceClass = field.getType();
                String clientBeanName = field.getName();
                String serviceExportName = interfaceClass.getName();

                if (!interfaceClass.isInterface()) {
                    throw new FatalBeanException(
                            "Exception initializing @RemoteResource client for " + clientBeanName + " must be a interface.");
                }

                Remote remote = AnnotationUtils.findAnnotation(interfaceClass, Remote.class);
                if (remote == null) {
                    // 注册的接口必须要加@Remote
                    throw new FatalBeanException("interface " + interfaceClass + " must with @Remote");
                }

                if (StringUtils.isEmpty(remote.applicationName())) {
                    throw new FatalBeanException("interface " + interfaceClass + " must with applicationName");
                }

                String serviceUrl = StringUtils.isEmpty(url) ? remote.applicationName() : url;

                if (connectTimeout <= 0 && remote.connectTimeout() > 0) {
                    connectTimeout = remote.connectTimeout();
                }
                if (readTimeout <= 0 && remote.readTimeout() > 0) {
                    readTimeout = remote.readTimeout();
                }

                // 禁止本模块rpc调用本模块函数
                ApplicationContext ctx = SpringContext.getApplicationContext();
                String appName = ctx.getEnvironment().getProperty("spring.application.name");
                if (serviceUrl.equals(appName)) {
                    throw new FatalBeanException(field.getName() + " should use '@Autowired' in " + bean.getClass());
                }

                Object obj = ProxyBuilder
                        .create()
                        .setExportName(serviceExportName)
                        .setApplicationName(serviceUrl)
                        .setInterfaceClass(interfaceClass)
                        .setConnectTimeout(connectTimeout)
                        .setReadTimeout(readTimeout)
                        .setInClass(bean.getClass())
                        .build();
                try {
                    field.setAccessible(true);
                    field.set(bean, obj);
                } catch (IllegalAccessException e) {
                    throw new FatalBeanException(
                            "Exception initializing remoting client for " + clientBeanName + " in " + beanName, e);
                }
            }
        }
    }
}
