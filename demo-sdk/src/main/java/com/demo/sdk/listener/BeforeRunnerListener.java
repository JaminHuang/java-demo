package com.demo.sdk.listener;

import com.demo.sdk.runner.BeforeRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * 前置runner 启动
 * 
 * @author caiLinFeng
 * @date 2018年5月14日
 */
@Configuration
public class BeforeRunnerListener implements ApplicationListener<ApplicationPreparedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(BeforeRunnerListener.class);

	@Autowired(required = false)
	private List<BeforeRunner> beforeRunners;

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		logger.info("====================BeforeRunner启动====================");
		if(beforeRunners == null || beforeRunners.isEmpty()){
			return;
		}
		Collections.sort(beforeRunners);
		for (BeforeRunner e : beforeRunners) {
			logger.info("BeforeRunner 启动  {}, 顺序  {}", e.getClass().getName(), e.getOrder());
			e.execute();
		}
		logger.info("{}个BeforeRunner执行完毕", beforeRunners.size());
	}

}
