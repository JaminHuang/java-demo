package com.demo.ibatx.build;

import com.demo.ibatx.core.mapper.BaseMapper;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展mapper注册器
 */
public class ExtMapperRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ExtMapperRegistry.class);

    private Configuration configuration;

    public ExtMapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 注册所有的
     *
     * @param type 需要注册的Mapper
     * @param <T>  mapper的类型
     */
    public <T> void addMapper(Class<T> type) {
        if (!configuration.hasMapper(type)) {
            configuration.addMapper(type);
        }
        this.addMapperOnlyExt(type);
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.configuration.setUseGeneratedKeys(useGeneratedKeys);
    }

    /**
     * 只注册扩展的
     *
     * @param type 需要注册的Mapper
     * @param <T>  mapper的类型
     */
    public <T> void addMapperOnlyExt(Class<T> type) {
        if (!BaseMapper.class.isAssignableFrom(type)) {
            return;
        }
        ExtMapperAnnotationBuilder extMapperAnnotationBuilder = new ExtMapperAnnotationBuilder(configuration, type);
        extMapperAnnotationBuilder.addMapperOnlyExt(type);
        logger.info("成功加载ibatx Mapper = {}", type);
    }

}
