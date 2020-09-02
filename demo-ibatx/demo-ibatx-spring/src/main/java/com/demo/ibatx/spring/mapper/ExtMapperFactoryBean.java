package com.demo.ibatx.spring.mapper;

import com.demo.ibatx.build.ExtMapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.MapperFactoryBean;

/**
 * 继承spring mybatis 原生的MapperFactoryBean 对 spring 的生命周期不做改动，制作扩张
 */
public class ExtMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public ExtMapperFactoryBean() {
    }

    public ExtMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        Configuration configuration = getSqlSession().getConfiguration();
        ExtMapperRegistry extMapperRegistry = new ExtMapperRegistry(configuration);
        extMapperRegistry.addMapperOnlyExt(getMapperInterface());
    }

}
