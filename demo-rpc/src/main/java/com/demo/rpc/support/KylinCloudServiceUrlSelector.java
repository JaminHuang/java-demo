package com.demo.rpc.support;

import com.demo.rpc.client.ServiceUrlSelector;
import com.demo.rpc.client.exceptions.NoServiceUrlFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

/**
 * 生产环境
 */
@Component
public class KylinCloudServiceUrlSelector implements ServiceUrlSelector {

    private Logger logger = LoggerFactory.getLogger(KylinCloudServiceUrlSelector.class);

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Override
    public String selectUrl(String serviceName) throws NoServiceUrlFoundException {

        ServiceInstance serviceInstance = null;
        // 如果获取实例失败则尝试获取16次
        for (int i = 0; i < 16; i++) {
            serviceInstance = loadBalancer.choose(serviceName);
            if (serviceInstance == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        if (serviceInstance == null) {
            throw new NoServiceUrlFoundException(
                    "No valid serviceUrl find from RegServer by serviceName: " + serviceName);
        }

        return serviceInstance.getUri().toString();

    }

}
