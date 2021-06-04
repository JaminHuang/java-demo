package com.demo.sdk.apollo;

import java.util.Set;

/**
 * key 解析器
 */
public interface KeyParser {

    /**
     * 自定义解析key-value,并赋值成员变量
     *
     * @param key
     * @param value
     */
    void parse(String key, String value);

    /**
     * @return 需要关心的keys
     */
    Set<String> keys();

}
