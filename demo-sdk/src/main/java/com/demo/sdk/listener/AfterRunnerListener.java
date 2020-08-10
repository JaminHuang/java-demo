package com.demo.sdk.listener;

import com.demo.sdk.runner.AfterRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * 后置runner 启动
 * 
 * @author caiLinFeng
 * @date 2018年5月14日
 */
@Configuration
public class AfterRunnerListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger logger = LoggerFactory.getLogger(AfterRunnerListener.class);

	@Autowired(required = false)
	private List<AfterRunner> afterRunners;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info("====================AfterRunner启动====================");
		if (afterRunners == null || afterRunners.isEmpty()) {
			return;
		}
		Collections.sort(afterRunners);
		for (AfterRunner e : afterRunners) {
			logger.info("AfterRunner 启动  {}, 顺序  {}", e.getClass().getName(), e.getOrder());
			e.execute();
		}
		logger.info("{}个AfterRunner执行完毕", afterRunners.size());
	}

}
