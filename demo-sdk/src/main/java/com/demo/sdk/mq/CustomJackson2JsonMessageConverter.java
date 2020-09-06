package com.demo.sdk.mq;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

/**
 * 自定义消息转换
 */
public class CustomJackson2JsonMessageConverter extends AbstractJsonMessageConverter {

    private static Log log = LogFactory.getLog(CustomJackson2JsonMessageConverter.class);
    private ObjectMapper objectMapper;
    private JavaType javaType;

    public CustomJackson2JsonMessageConverter() {
        this.javaType = this.getJavaType(Object.class);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public Object fromMessage(org.springframework.amqp.core.Message message) throws MessageConversionException {
        byte[] bytes = message.getBody();
        try {
            return this.objectMapper.readValue(bytes, 0, bytes.length, this.javaType);
        } catch (Exception var3) {
            throw new RuntimeException("Could not read JSON: " + var3.getMessage(), var3);
        }
    }

    protected org.springframework.amqp.core.Message createMessage(Object objectToConvert, MessageProperties messageProperties) throws MessageConversionException {
        byte[] bytes;
        try {
            if (objectToConvert == null) {
                bytes = new byte[0];
            } else {
                try {
                    bytes = this.objectMapper.writeValueAsBytes(objectToConvert);
                } catch (Exception var3) {
                    throw new RuntimeException("Could not write JSON: " + var3.getMessage(), var3);
                }
            }
        } catch (Exception var5) {
            throw new MessageConversionException("Failed to convert Message content", var5);
        }
        messageProperties.setContentType("application/json");
        messageProperties.setContentEncoding(this.getDefaultCharset());
        messageProperties.setContentLength((long) bytes.length);
        return new Message(bytes, messageProperties);
    }

    private JavaType getJavaType(Class<?> clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }
}