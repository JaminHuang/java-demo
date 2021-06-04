package com.demo.sdk.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.demo.sdk.exception.IllegalKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class KeyHolder {

    private static final Logger logger = LoggerFactory.getLogger(KeyHolder.class);

    /**
     * key与实例的映射
     */
    private static HashMap<String, Set<KeyBean>> beanMap = new HashMap<>();

    /**
     * key与parser的映射
     */
    private static HashMap<String, Set<KeyParser>> parserMap = new HashMap<>();

    static synchronized void put(KeyBean keyBean) {
        String key = keyBean.getKey();
        Set<KeyBean> beans = beanMap.get(key);
        if (beans == null) {
            beans = new HashSet<>();
        }
        beans.add(keyBean);
        beanMap.put(key, beans);
    }

    static synchronized void put(KeyParser keyParser) {
        Set<String> keys = keyParser.keys();
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            Set<KeyParser> parsers = parserMap.get(key);
            if (parsers == null) {
                parsers = new HashSet<>();
            }
            parsers.add(keyParser);
            parserMap.put(key, parsers);
        }
    }

    public static HashMap<String, Set<KeyBean>> getBeanMap() {
        return beanMap;
    }

    public static HashMap<String, Set<KeyParser>> getParserMap() {
        return parserMap;
    }

    static synchronized void init() {
        long startMillis = System.currentTimeMillis();
        Config config = ConfigService.getConfig(ConfigContext.getConfig());
        if (config == null) {
            throw new IllegalArgumentException("apollo no " + ConfigContext.getConfig() + " namespace");
        }
        // assign
        Set<String> keys = beanMap.keySet();
        for (String key : keys) {
            Set<KeyBean> keyBeans = beanMap.get(key);
            String value = config.getProperty(key, null);
            assign(keyBeans, value);
        }
        // parse
        keys = parserMap.keySet();
        for (String key : keys) {
            Set<KeyParser> keyParsers = parserMap.get(key);
            String value = config.getProperty(key, null);
            parse(keyParsers, key, value);
        }
        long endMillis = System.currentTimeMillis();
        logger.info("@Key init 耗时{}ms", endMillis - startMillis);
    }

    static synchronized void update(String key, String value) {
        // assign
        Set<KeyBean> keyBeans = beanMap.get(key);
        assign(keyBeans, value);
        // parse
        Set<KeyParser> keyParsers = parserMap.get(key);
        parse(keyParsers, key, value);
    }

    private static void assign(Set<KeyBean> keyBeans, String value) {
        if (keyBeans == null || keyBeans.isEmpty()) {
            return;
        }
        if (value == null) {
            String classes = "";
            for (KeyBean keyBean : keyBeans) {
                classes += keyBean.getBean().getClass().getName() + ",";
            }
            classes = classes.substring(0, classes.length() - 1);
            logger.error("@Key(\"" + keyBeans.iterator().next().getKey() + "\") value is null, in classes : " + classes);
            throw new IllegalKeyException("@Key(\"" + keyBeans.iterator().next().getKey() + "\") value is null, in classes : " + classes);
        }
        for (KeyBean keyBean : keyBeans) {
            KeyAssigner.assign(
                    keyBean.getBean(),
                    keyBean.getField(),
                    value);
        }
    }

    private static void parse(Set<KeyParser> keyParsers, String key, String value) {
        if (keyParsers == null || keyParsers.isEmpty()) {
            return;
        }
        if (value == null) {
            String classes = "";
            for (KeyParser keyParser : keyParsers) {
                classes += keyParser.getClass().getName() + ",";
            }
            classes = classes.substring(0, classes.length() - 1);
            logger.error("value of KeyParser  " + key + "is null, in classes : " + classes);
            throw new IllegalKeyException("value of KeyParser is null, in classes : " + classes);
        }
        for (KeyParser keyParser : keyParsers) {
            keyParser.parse(key, value);
        }
    }

}
