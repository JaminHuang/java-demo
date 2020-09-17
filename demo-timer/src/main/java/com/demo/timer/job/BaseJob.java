package com.demo.timer.job;

import com.demo.sdk.thread.ReqThreadLocal;
import com.demo.sdk.util.ExceptionUtils;
import com.demo.sdk.util.StringUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务管理
 *
 * @author caiLinFeng
 * @date 2018年10月16日
 */
public abstract class BaseJob extends IJobHandler {

    protected Logger logger;

    public BaseJob() {
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public final ReturnT<String> execute(String param) throws Exception {
        ReturnT<String> ret = FAIL;
        long start = System.currentTimeMillis();
        ReqThreadLocal.setTid(StringUtils.generateTraceId());
        if (JobContainer.isWork()) {
            JobContainer.incr();
            try {
                run(param);
                ret = SUCCESS;
            } catch (Exception e) {
                logger.error(ExceptionUtils.getExceptionMsg(e));
            }
            JobContainer.decr();
        } else {
            XxlJobLogger.log("服务优雅重启中...");
        }

        long end = System.currentTimeMillis();
        ReqThreadLocal.removeAll();
        XxlJobLogger.log("耗时{}ms", end - start);
        return ret;
    }

    protected abstract void run(String param);


}
