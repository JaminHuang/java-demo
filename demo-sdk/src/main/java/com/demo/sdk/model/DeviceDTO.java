package com.demo.sdk.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceDTO implements Serializable {

    private static final long serialVersionUID = -2527763814995013025L;

    /**
     * 0-默认 1-Android 2-iOS 3-Windows
     */
    private Byte device;

    private String appVersion;

    private String osVersion;

    public enum Device {
        DEFAULT(0, "默认"),
        ANDROID(1, "安卓"),
        IOS(2, "IOS"),
        WINDOWS(3, "Windows");

        private Byte value;
        private String name;

        Device(int value, String name) {
            this.value = (byte) value;
            this.name = name;
        }

        public Byte getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}
