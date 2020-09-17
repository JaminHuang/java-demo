package com.demo.timer.job;


import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@JobHandler("demoJob")
@Service
public class DemoJob extends com.demo.timer.job.BaseJob {

    private static final Logger logger = LoggerFactory.getLogger(DemoJob.class);

    @Override
    protected void run(String param) {
        logger.info("demoJob execute param = {}", param);
    }
}
