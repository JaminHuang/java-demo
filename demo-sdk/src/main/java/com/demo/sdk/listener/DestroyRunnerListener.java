package com.demo.sdk.listener;

import com.demo.sdk.runner.DestroyRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Collections;
import java.util.List;

/**
 * 后置runner 启动
 *
 * @author caiLinFeng
 * @date 2018年5月14日
 */
@Configuration
public class DestroyRunnerListener implements ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DestroyRunnerListener.class);

    @Autowired(required = false)
    private List<DestroyRunner> destroyRunners;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("====================DestroyRunner启动====================");
        if (destroyRunners == null || destroyRunners.isEmpty()) {
            return;
        }
        Collections.sort(destroyRunners);
        for (DestroyRunner e : destroyRunners) {
            logger.info("DestroyRunner 启动  {}, 顺序  {}", e.getClass().getName(), e.getOrder());
            e.execute();
        }
        logger.info("{}DestroyRunner", destroyRunners.size());
    }

}
