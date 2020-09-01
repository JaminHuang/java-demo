package com.demo.sdk.consts;

public enum RoleEnum {

    /**
     * 超管角色
     */
    ADMIN("role_admin");

    private String desc;

    private RoleEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
