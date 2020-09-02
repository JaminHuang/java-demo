package com.demo.ibatx.core.annotation;

/**
 * 主键策略的类型
 */
public enum GenerationType {

//    /**
//     * Indicates that the persistence provider must assign
//     * primary keys for the entity using an underlying
//     * database table to ensure uniqueness.
//     */
//    TABLE,

//    /**
//     * Indicates that the persistence provider must assign
//     * primary keys for the entity using database sequence column.
//     */
//    SEQUENCE,

    /**
     * 自增主键
     */
    IDENTITY,

    /**
     * 主键可以是提供的
     */
    PROVIDED

}
