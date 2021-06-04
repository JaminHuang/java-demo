
package com.demo.sdk.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonUtils {

    private static ObjectMapper JSON = new ObjectMapper();

    static {
        JSON.setSerializationInclusion(Include.NON_NULL);
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSON.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    public static String getJsonFromObject(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            return JSON.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> getListFromJson(String json, Class<T> valueType) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        try {

            return com.alibaba.fastjson.JSON.parseArray(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static <T> T getObjectFromJson(String json, Class<T> valueType) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return JSON.readValue(json, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getObjectFromJson(String json, TypeReference<T> valueTupeRef) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return JSON.readValue(json, valueTupeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
