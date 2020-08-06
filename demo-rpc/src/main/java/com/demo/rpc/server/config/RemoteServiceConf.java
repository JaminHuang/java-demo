package com.demo.rpc.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

/**
 * description
 *
 * @author jamin
 * @date 2020/8/6
 */
@Configuration
public class RemoteServiceConf {

    public static final String REMOTE_SERVICE_HANDLER_MAPPING_NAME = "remoteServiceHandlerMapping";

    @Bean("remoteServiceHandlerMapping")
    public SimpleUrlHandlerMapping remoteServiceHandlerMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setOrder(2);
        return simpleUrlHandlerMapping;
    }

}