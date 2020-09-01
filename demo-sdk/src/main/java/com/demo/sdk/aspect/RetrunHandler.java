package com.demo.sdk.aspect;

import com.demo.sdk.annotation.Data;
import com.demo.sdk.response.Result;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class RetrunHandler {
    /**
     * 默认排除字段，Page类属性
     */
    private static final List<String> DEFAULT_EXCLUDES = Arrays.asList("num", "row", "start", "sort", "sortType",
            "version", "MAX_ROW");

    /**
     * 默认的包前缀
     */
    private static final List<String> DEFAULT_PACKAGES = Arrays.asList("com.ac", "com.pdabc", "com.hp");

    /**
     * 处理返回值
     *
     * @param
     * @throws
     * @author caiLinFeng
     * @date 2018年3月7日
     */
    static void handle(JoinPoint joinPoint, Object obj) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException {
        if (obj == null) {
            return;
        }
        Class clz = Result.class;
        if (clz != obj.getClass()) {
            return;
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        List<String> include = null;
        List<String> exclude = new ArrayList<>(DEFAULT_EXCLUDES);

        Data dataAnnotation = method.getAnnotation(Data.class);

        if (dataAnnotation != null) {
            if (dataAnnotation.include().length != 0) {
                include = new ArrayList<>();
                for (String e : dataAnnotation.include()) {
                    include.add(e);
                }
            }
            if (dataAnnotation.exclude().length != 0) {
                for (String e : dataAnnotation.exclude()) {
                    exclude.add(e);
                }
            }
        }

        Field dataField = clz.getDeclaredField("data");
        dataField.setAccessible(true);
        // 获取data对象
        Object data = dataField.get(obj);
        if (data == null) {
            return;
        }

        handleData(data, include, exclude);
    }

    /**
     * 根据include,exclude 处理目标对象
     *
     * @param
     * @author caiLinFeng
     * @date 2018年3月7日
     */
    private static void handleData(Object targetObject, List<String> include, List<String> exclude)
            throws IllegalArgumentException, IllegalAccessException {
        // Collection 类型
        if (targetObject == null) {
            return;
        }
        if (targetObject instanceof Collection<?>) {
            Collection<?> collect = (Collection<?>) targetObject;
            for (Object e : collect) {
                handleData(e, include, exclude);
            }
        }

        // Map 类型
        if (targetObject instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) targetObject;
            Collection<?> values = map.values();
            if (values != null) {
                for (Object e : values) {
                    handleData(e, include, exclude);
                }
            }
        }

        if (!deducePOVO(targetObject.getClass())) {
            return;
        }

        Set<Field> fieldSet = getDeclaredFields(targetObject.getClass());

        for (Field e : fieldSet) {
            e.setAccessible(true);

            Object obj = e.get(targetObject);

            // Collection 类型
            if (obj instanceof Collection<?>) {
                Collection<?> collect = (Collection<?>) obj;
                for (Object e2 : collect) {
                    handleData(e2, include, exclude);
                }
            }

            // Map 类型
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                Collection<?> values = map.values();
                if (values != null) {
                    for (Object e2 : values) {
                        handleData(e2, include, exclude);
                    }
                }
            } else if (deducePOVO(e.getType())) {
                Object obj2 = e.get(targetObject);
                handleData(obj2, include, exclude);
            }
            // 判断执行include or exclude
            if (include != null && include.size() != 0) {
                if (!include.contains(e.getName()) && !e.getType().isPrimitive() && !Modifier.isFinal(e.getModifiers())) {
                    e.set(targetObject, null);
                }
            } else {
                if (exclude.contains(e.getName()) && !e.getType().isPrimitive() && !Modifier.isFinal(e.getModifiers())) {
                    e.set(targetObject, null);
                }
            }
        }

    }

    /**
     * 获取class所有成员属性
     *
     * @param
     * @author caiLinFeng
     * @date 2018年3月7日
     */
    private static Set<Field> getDeclaredFields(Class<?> clazz) {
        Set<Field> fieldSet = new HashSet<>();
        while (clazz != null) {
            fieldSet.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldSet;
    }


    /**
     * 判断是否是com.ac、com.pdabc包路径下的类
     *
     * @param
     * @author caiLinFeng
     * @date 2019年07月23日
     */
    private static boolean deducePOVO(Class<?> clazz) {
        if (clazz != null && clazz.getPackage() != null) {
            String packageName = clazz.getPackage().getName();
            // 取类路径的前面2个
            StringBuilder sb = new StringBuilder();
            String[] arr = packageName.split("\\.");
            for (int i = 0; i < arr.length; i++) {
                if (i == 2) {
                    break;
                }
                if (!"".equals(sb.toString())) {
                    sb.append(".");
                }
                sb.append(arr[i]);
            }
            return DEFAULT_PACKAGES.contains(sb.toString());
        }
        return false;
    }

}
