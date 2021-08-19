package com.demo.sdk.thread;

import com.alibaba.fastjson.JSONObject;
import com.demo.sdk.consts.IsDebug;
import com.demo.sdk.consts.SystemConstant;
import com.demo.sdk.model.DeviceDTO;

/**
 * description
 *
 * @author jamin
 * @date 2020/8/6
 */
public class ReqThreadLocal {
    /**
     * accessToken
     */
    private final static ThreadLocal<String> ACCESS_TOKEN = new ThreadLocal<>();

    /**
     * 请求用户的id
     */
    private final static ThreadLocal<Integer> USER_ID = ThreadLocal.withInitial(() -> 0);

    /**
     * 请求用户的名字
     */
    private final static ThreadLocal<String> USER_NAME = ThreadLocal.withInitial(() -> SystemConstant.DEFAULT_SYSTEM_OPERATOR);

    /**
     * 请求的ip地址
     */
    private final static ThreadLocal<String> IP = ThreadLocal.withInitial(() -> "unknown");

    /**
     * 请求的user
     */
    private final static ThreadLocal<JSONObject> USER = new ThreadLocal<>();

    /**
     * 请求的设备信息
     */
    private final static ThreadLocal<DeviceDTO> DEVICE = ThreadLocal.withInitial(DeviceDTO::new);

    /**
     * 请求的query
     */
    private final static ThreadLocal<String> QUERY = new ThreadLocal<>();

    /**
     * 请求的body
     */
    private final static ThreadLocal<String> BODY = new ThreadLocal<>();

    /**
     * 链路id
     */
    private final static ThreadLocal<String> TID = new ThreadLocal<>();

    /**
     * 调试模式参数
     */
    private final static ThreadLocal<Byte> DEBUG = ThreadLocal.withInitial(() -> IsDebug.NOT_DEBUG);

    public static void removeAll() {
        ACCESS_TOKEN.remove();
        USER_ID.remove();
        USER_NAME.remove();
        IP.remove();
        DEVICE.remove();
        USER.remove();
        QUERY.remove();
        BODY.remove();
        TID.remove();
        DEBUG.remove();
    }

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN.set(accessToken);
    }

    public static String getAccessToken() {
        return ACCESS_TOKEN.get();
    }

    public static void setUserId(Integer userId) {
        USER_ID.set(userId);
    }

    public static Integer getUserId() {
        return USER_ID.get();
    }

    public static void setIp(String ip) {
        IP.set(ip);
    }

    public static String getIp() {
        return IP.get();
    }

    public static void setUserName(String userName) {
        USER_NAME.set(userName);
    }

    public static String getUserName() {
        return USER_NAME.get();
    }

    public static void setUser(JSONObject user) {
        USER.set(user);
    }

    public static JSONObject getUser() {
        return USER.get();
    }

    public static void setQuery(String query) {
        QUERY.set(query);
    }

    public static String getQuery() {
        return QUERY.get();
    }

    public static void setBody(String body) {
        BODY.set(body);
    }

    public static String getBody() {
        return BODY.get();
    }

    public static void setTid(String tid) {
        TID.set(tid);
    }

    public static String getTid() {
        return TID.get();
    }

    public static void setDebug(Byte debug) {
        DEBUG.set(debug);
    }

    public static Byte getDebug() {
        return DEBUG.get();
    }

    public static void setDevice(DeviceDTO deviceDTO) {
        DEVICE.set(deviceDTO);
    }

    public static DeviceDTO getDevice() {
        return DEVICE.get();
    }
}
