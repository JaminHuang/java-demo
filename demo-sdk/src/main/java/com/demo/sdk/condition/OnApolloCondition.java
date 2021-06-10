package com.demo.sdk.condition;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.demo.sdk.apollo.ConfigContext;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

class OnApolloCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionOnApollo.class.getName());
        String key = (String) annotationAttributes.get("key");
        String value = (String) annotationAttributes.get("value");
        Compare compare = (Compare) annotationAttributes.get("compare");
        // 获取namespace
        Set<String> namespaces = ConfigContext.getNamespaces();
        boolean result = false;
        for (String namespace : namespaces) {
            Config config = ConfigService.getConfig(namespace);
            if (config == null) {
                return false;
            }
            String apolloValue = config.getProperty(key, null);
            // 比较相等
            if (Compare.EQUAL.equals(compare)) {
                result = value.equals(apolloValue);
            }
            // 比较是否存在
            else if (Compare.NOT_EMPTY.equals(compare)) {
                result = !StringUtils.isEmpty(apolloValue);
            }
            if (result) {
                return true;
            }
        }
        return false;
    }

}
