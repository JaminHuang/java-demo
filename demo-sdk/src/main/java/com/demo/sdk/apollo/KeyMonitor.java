package com.demo.sdk.apollo;

import com.alibaba.fastjson.JSONObject;
import com.demo.sdk.runner.DestroyRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 监控Key的使用
 */
@Component
public class KeyMonitor extends DestroyRunner implements ApplicationListener<ApplicationPreparedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(KeyMonitor.class);

    private static final String KEY_CONFIG_MONITOR = "key_config_monitor";

    private List<String> keyNames;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private String keyApplicationName;

    @Value("${spring.application.name}")
    private String applicationName;

    public KeyMonitor() {
        this.keyNames = new ArrayList<>();
    }

    @Override
    public void execute() {
        logger.info("删除Key使用应用:{}", keyApplicationName);
        if (Objects.isNull(keyApplicationName)) {
            return;
        }
        redisTemplate.opsForHash().delete(KEY_CONFIG_MONITOR, keyApplicationName);
    }

    private void addKey(String keyName) {
        this.keyNames.add(keyName);
    }

    private void addKeys(Set<String> keySets) {
        this.keyNames.addAll(keySets);
    }

    private void sync() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("获取本地IP失败");
            ip = "UNKNOW";
        }
        this.keyApplicationName = applicationName + ":" + ip;
        this.addKeys(KeyHolder.getBeanMap().keySet());
        this.addKeys(KeyHolder.getParserMap().keySet());
        redisTemplate.opsForHash().put(KEY_CONFIG_MONITOR, keyApplicationName, JSONObject.toJSONString(this.keyNames));
        logger.info("同步Key使用应用:{}:keys:{}", keyApplicationName, this.keyNames);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        this.sync();
    }

//    @Override
//    public void setEnvironment(Environment environment) {
//        this.applicationName = environment.getProperty("spring.application.name");
//    }

}
