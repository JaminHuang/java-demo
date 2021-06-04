package com.demo.sdk.apollo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.demo.sdk.exception.BizException;
import com.demo.sdk.util.ExceptionUtils;
import com.demo.sdk.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Key的赋值操作
 */
public class KeyAssigner {

    private static final Logger logger = LoggerFactory.getLogger(KeyAssigner.class);

    /**
     * 参数赋值
     *
     * @param
     * @author caiLinFeng
     * @date 2018年11月24日
     */
    static void assign(Object bean, String fieldName, String value) {
        Field field = null;
        try {
            field = bean.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.error("[error] = {}", ExceptionUtils.getExceptionMsg(e));
            throw new RuntimeException("NoSuchField");
        }
        field.setAccessible(true);
        String simpleName = field.getType().getSimpleName();
        try {
            // byte
            if (Arrays.asList("byte", "Byte").contains(simpleName)) {
                if ("Byte".equals(simpleName)) {
                    field.set(bean, Byte.valueOf(value));
                } else {
                    field.setByte(bean, Byte.valueOf(value));
                }
            }
            // short
            else if (Arrays.asList("short", "Short").contains(simpleName)) {
                if ("Short".equals(simpleName)) {
                    field.set(bean, Short.valueOf(value));
                } else {
                    field.setShort(bean, Short.valueOf(value));
                }
            }
            // int
            else if (Arrays.asList("int", "Integer").contains(simpleName)) {
                if ("Integer".equals(simpleName)) {
                    field.set(bean, Integer.valueOf(value));
                } else {
                    field.setInt(bean, Integer.valueOf(value));
                }
            }
            // long
            else if (Arrays.asList("long", "Long").contains(simpleName)) {
                if ("Long".equals(simpleName)) {
                    field.set(bean, Long.valueOf(value));
                } else {
                    field.setLong(bean, Long.valueOf(value));
                }
            }
            // float
            else if (Arrays.asList("float", "Float").contains(simpleName)) {
                if ("Float".equals(simpleName)) {
                    field.set(bean, Float.valueOf(value));
                } else {
                    field.setFloat(bean, Float.valueOf(value));
                }
            }
            // double
            else if (Arrays.asList("double", "Double").contains(simpleName)) {
                if ("Double".equals(simpleName)) {
                    field.set(bean, Double.valueOf(value));
                } else {
                    field.setDouble(bean, Double.valueOf(value));
                }
            }
            // boolean
            else if (Arrays.asList("boolean", "Boolean").contains(simpleName)) {
                if ("Boolean".equals(simpleName)) {
                    field.set(bean, Boolean.valueOf(value));
                } else {
                    field.setBoolean(bean, Boolean.valueOf(value));
                }
            }
            // String
            else if ("String".equals(simpleName)) {
                field.set(bean, value);
            }
            // list
            else if ("List".equals(simpleName)) {
                List list = JsonUtils.getListFromJson(value, getGenericsFromCollection(field));
                field.set(bean, list);
            }
            // set
            else if ("Set".equals(simpleName)) {
                List list = JsonUtils.getListFromJson(value, getGenericsFromCollection(field));
                if (list != null) {
                    field.set(bean, Sets.newHashSet(list));
                }
            }
            // map
            else if ("Map".equals(simpleName)) {
                Map map = JSONObject.parseObject(value);
                field.set(bean, map);
            }
            // Object类型
            else {
                Object obj = JsonUtils.getObjectFromJson(value, field.getType());
                field.set(bean, obj);
            }

            logger.info("-->@key assign [bean] = {}, [field] = {}, [value]= {}",
                    bean, field.getName(), value);
        } catch (NumberFormatException e) {
            logger.error(ExceptionUtils.getExceptionMsg(e));
            throw BizException.getInstance("NumberFormatException, [field] = {} in {}", field.getName(),
                    bean.getClass());
        } catch (IllegalArgumentException e) {
            logger.error(ExceptionUtils.getExceptionMsg(e));
            throw BizException.getInstance("IllegalArgumentException, [field] = {} in {}", field.getName(),
                    bean.getClass());
        } catch (IllegalAccessException e) {
            logger.error(ExceptionUtils.getExceptionMsg(e));
            throw BizException.getInstance("IllegalAccessException, [field] = {} in {}", field.getName(),
                    bean.getClass());
        }
    }

    /**
     * 从集合属性获取泛型
     *
     * @param field
     * @return
     */
    private static Class<?> getGenericsFromCollection(Field field) {
        Type gType = field.getGenericType();
        if (gType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) gType;
            // 取得泛型类型的泛型参数
            Type[] tArgs = pType.getActualTypeArguments();
            if (tArgs.length != 0) {
                return (Class<?>) tArgs[0];
            }
        }
        throw BizException.getInstance("getGenericsFromCollection error, [field] = {} in {}", field.getName());
    }

    /**
     * 从Map属性获取泛型
     *
     * @param field
     * @return
     */
    private static Class<?>[] getGenericsFromMap(Field field) {
        Type gType = field.getGenericType();
        if (gType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) gType;
            // 取得泛型类型的泛型参数
            Type[] tArgs = pType.getActualTypeArguments();
            if (tArgs.length == 2) {
                return new Class<?>[]{(Class<?>) tArgs[0], (Class<?>) tArgs[1]};
            }
        }
        throw BizException.getInstance("getGenericsFromCollection error, [field] = {} in {}", field.getName());
    }

}
