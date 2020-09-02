package com.demo.ibatx.meta;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;

/**
 * 主键字段
 */
public class PrimaryKeyField extends EntityField {


    /**
     * 主键生成器， 只支持自增主键的情况
     */
    private KeyGenerator keyGenerator = Jdbc3KeyGenerator.INSTANCE;

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
}
