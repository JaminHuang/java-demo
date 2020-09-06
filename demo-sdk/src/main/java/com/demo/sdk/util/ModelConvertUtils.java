package com.demo.sdk.util;

import com.demo.sdk.exception.BizException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModelConvertUtils {

    public static <T> T convert(Class<T> type, Object o) {
        try {
            T t = type.newInstance();
            BeanUtils.copyProperties(o, t);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("对象convert出错");
        }

    }

    public static <T, V> List<T> convertList(Class<T> target, List<V> list) {
        if (list == null) {
            throw new IllegalArgumentException("list is empty");
        }
        List<T> targetList = new ArrayList<T>();

        list.stream().forEach(e -> {
            targetList.add(convert(target, e));

        });

        return targetList;
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
