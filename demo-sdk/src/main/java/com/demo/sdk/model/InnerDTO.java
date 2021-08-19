package com.demo.sdk.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class InnerDTO implements Serializable {

    private static final long serialVersionUID = 5328837821162220794L;

    /**
     * 签名 appid
     */
    private String innerAppId;

    /**
     * 签名密钥
     */
    private String innerAppSecret;

    /**
     * 描述
     */
    private String description;

    public InnerDTO(String innerAppId, String innerAppSecret, String description) {
        this.innerAppId = innerAppId;
        this.innerAppSecret = innerAppSecret;
        this.description = description;
    }
}
