package com.demo.timer.runner;

import com.demo.sdk.runner.DestroyRunner;
import com.demo.timer.job.JobContainer;
import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobEndRunner extends DestroyRunner {

    private static final Logger logger = LoggerFactory.getLogger(JobEndRunner.class);

    private static final long waitTime = 180;

    @Autowired
    private XxlJobSpringExecutor xxlJobSpringExecutor;

    @Override
    public void execute() {
        try {
            logger.info("JOB stop start");
            logger.info("向admin取消注册执行器");
            if (!JobContainer.awaitTermination(waitTime)) {
                logger.error("JOB 进程在{}秒内无法结束,尝试强制结束", waitTime);
            }
            xxlJobSpringExecutor.destroy();
            logger.info("JOB stop success");
        } catch (InterruptedException e) {
            logger.error("JOB 等待线程被打断");
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
