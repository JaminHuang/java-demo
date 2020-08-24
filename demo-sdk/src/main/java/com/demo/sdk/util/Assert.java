package com.demo.sdk.util;

import com.demo.sdk.exception.IllegalObjectException;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 断言
 */
public class Assert {

    /**
     * 判断非空
     *
     * @param obj
     * @param message
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw IllegalObjectException.getInstance(message);
        }
    }

    /**
     * 判断非空
     *
     * @param obj
     * @param message
     * @param args
     */
    public static void notNull(Object obj, String message, Object... args) {
        if (obj == null) {
            throw IllegalObjectException.getInstance(message, args);
        }
    }

    /**
     * 判断非空
     *
     * @param collection
     * @param message
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw IllegalObjectException.getInstance(message);
        }
    }

    /**
     * 判断非空
     *
     * @param collection
     * @param message
     */
    public static void notEmpty(Collection<?> collection, String message, Object... args) {
        if (CollectionUtils.isEmpty(collection)) {
            throw IllegalObjectException.getInstance(message, args);
        }
    }
}
