package com.demo.sdk.util;

import com.google.common.collect.Lists;
import com.demo.sdk.page.Page;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 集合工具类
 */
public class CollectionUtils extends org.springframework.util.CollectionUtils {

    /**
     * 批量插入最大长度值
     */
    private static final Integer EVERY_INSERT_MAX_SIZE = 2;

    /**
     * 按照EVERY_INSERT_MAX_SIZE，分段拆分成多个小list
     *
     * @param originList 要拆分的List
     * @return List
     * @author huangtao
     * @date 2019/3/6 19:24
     **/
    public static <T> List<List<T>> convertMultList(List<T> originList) {
        List<List<T>> multList = Lists.newArrayList();
        if (isEmpty(originList)) {
            return multList;
        }

        // 若拆分的list，小于EVERY_INSERT_MAX_SIZE，则直接封装返回
        int allSize = originList.size();
        if (allSize <= EVERY_INSERT_MAX_SIZE) {
            multList.add(originList);
            return multList;
        }

        // 按照EVERY_INSERT_MAX_SIZE的长度值，拆分list
        int size = 0;
        while (size < originList.size()) {
            multList.add(originList.subList(size, Math.min(size + EVERY_INSERT_MAX_SIZE, originList.size())));
            size += EVERY_INSERT_MAX_SIZE;
        }
        return multList;
    }

    /**
     * 按照maxSize，分段拆分成多个小list
     *
     * @param originList 要拆分的List
     * @param maxSize    拆分的大小(没传、非正数、大于Page的MAX_VALUE，则都取MAX_VALUE)
     * @return List
     * @author huangtao
     * @date 2019/3/6 19:24
     **/
    public static <T> List<List<T>> convertMultList(List<T> originList, Integer maxSize) {
        List<List<T>> multList = Lists.newArrayList();
        if (isEmpty(originList)) {
            return multList;
        }

        maxSize = (maxSize == null || maxSize <= 0 || maxSize > Page.getMaxRow()) ? Page.getMaxRow() : maxSize;

        // 若拆分的list，小于EVERY_INSERT_MAX_SIZE，则直接封装返回
        int allSize = originList.size();
        if (allSize <= maxSize) {
            multList.add(originList);
            return multList;
        }

        // 按照EVERY_INSERT_MAX_SIZE的长度值，拆分list
        int size = 0;
        while (size < originList.size()) {
            multList.add(originList.subList(size, Math.min(size + maxSize, originList.size())));
            size += maxSize;
        }
        return multList;
    }

    /**
     * 去重并过滤流，返回list
     *
     * @param stream 要处理的流
     * @param defaultValue 过滤的值
     * @author huangtao
     * @date 2019/3/6 19:30
     * @return List
     **/
    public static <T> List<T> toListWithFilterAndDistinct(Stream<T> stream, Object defaultValue) {
        if (stream == null) {
            return null;
        }

        return stream.filter(a -> Objects.nonNull(a) && !Objects.equals(defaultValue, a)).distinct().collect(Collectors.toList());
    }

    /**
     * 过滤流，返回list
     *
     * @param stream 要处理的流
     * @param defaultValue 过滤的值
     * @author huangtao
     * @date 2019/3/6 19:30
     * @return List
     */
    public static <T> List<T> toListWithFilter(Stream<T> stream, Object defaultValue) {
        if (stream == null) {
            return null;
        }

        return stream.filter(a -> Objects.nonNull(a) && !Objects.equals(defaultValue, a)).collect(Collectors.toList());
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> T getLastElement(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }

}