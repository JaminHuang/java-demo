package com.demo.rpc.server.core;

import com.demo.rpc.annotation.Remote;
import com.demo.rpc.server.config.InterceptorConf;
import com.demo.rpc.server.config.RemoteServiceConf;
import com.demo.rpc.server.interceptor.RemoteInvocationExceptionInterceptor;
import com.demo.rpc.server.remote.HessianServiceExporter;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.HashMap;
import java.util.Map;

@Component
public class RemoteServiceAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
        implements BeanFactoryAware, PriorityOrdered {

    private final static String EXPORT_METHOD_NAME_SERVICE_INTERFACE = "serviceInterface";
    private final static String EXPORT_METHOD_NAME_SERVICE = "service";
    private final static String EXPORT_METHOD_NAME_INTERCEPTORS = "interceptors";
    private final static String EXPORT_SERVICE_BEAN_NAME_SUFFIX = "Exporter";
    private int order;
    private DefaultListableBeanFactory beanFactory;
    private SimpleUrlHandlerMapping remotingServiceHandlerMapping;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Remote remoteAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), Remote.class);

        if (remoteAnnotation != null) {

            Class<?> interfaceClass = null;
            for (Class<?> iclazz : bean.getClass().getInterfaces()) {
                if (iclazz.getDeclaredAnnotation(Remote.class) != null) {
                    interfaceClass = iclazz;
                    break;
                }
            }
            if (interfaceClass == null) {
                throw new FatalBeanException("Exception initializing remoting service for " + beanName
                        + " @Remote should define on the interface.");
            }

            registerExportBean(beanName, interfaceClass);

        }

        return bean;
    }

    private void registerExportBean(String beanName, Class<?> interfaceClazz) {
        Class<? extends RemoteExporter> remoteExporterclass = HessianServiceExporter.class;
        String exportBeanName = beanName + EXPORT_SERVICE_BEAN_NAME_SUFFIX;
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder exportDeanDefinitionBuilder = BeanDefinitionBuilder
                .genericBeanDefinition(remoteExporterclass);
        exportDeanDefinitionBuilder.addPropertyValue(EXPORT_METHOD_NAME_SERVICE_INTERFACE, interfaceClazz);
        exportDeanDefinitionBuilder.addPropertyReference(EXPORT_METHOD_NAME_SERVICE, beanName);
        exportDeanDefinitionBuilder.addPropertyValue(EXPORT_METHOD_NAME_INTERCEPTORS,
                new Object[]{new RemoteInvocationExceptionInterceptor(ClassUtils.getShortName(remoteExporterclass)),
                        beanFactory.getBean(InterceptorConf.REMOTE_INVOCATION_MONITOR_INTERCEPTOR_NAME),
                        beanFactory.getBean(InterceptorConf.REMOTE_INVOCATION_LOGGER_INTERCEPTOR_NAME)});
        beanFactory.registerBeanDefinition(exportBeanName, exportDeanDefinitionBuilder.getBeanDefinition());

        Map<String, Object> umap = new HashMap<String, Object>();
        umap.put(interfaceClazz.getName(), beanFactory.getBean(exportBeanName));
        remotingServiceHandlerMapping.setUrlMap(umap);
        remotingServiceHandlerMapping.initApplicationContext();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        this.remotingServiceHandlerMapping = (SimpleUrlHandlerMapping) beanFactory
                .getBean(RemoteServiceConf.REMOTE_SERVICE_HANDLER_MAPPING_NAME);
    }
}
