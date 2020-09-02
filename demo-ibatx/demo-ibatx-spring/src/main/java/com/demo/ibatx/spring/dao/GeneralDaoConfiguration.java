package com.demo.ibatx.spring.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 通用dao的配置
 */
@Configuration
public class GeneralDaoConfiguration {


    @Bean
    public JdbcTemplate jdbcTemplate(@Autowired DataSource dataSource) {
        return new JdbcTemplate(dataSource, true);
    }

    @Bean
    public BasicDao simpleDao(@Autowired JdbcTemplate jdbcTemplate, @Autowired SqlSessionFactory sqlSessionFactory) {
        return new GeneralDaoImpl(jdbcTemplate, sqlSessionFactory.getConfiguration());
    }
}
