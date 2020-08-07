package com.demo.sdk.consts;

/**
 * 错误码
 */
public enum BErrorCode implements IErrorCode {

    OK(0, "success"),

    SERVER_STOPPING(33, "sever stopping"),

    UNDEFINE_ERROR(404, "未定义的错误"),

    ACCESS_TOKEN_ERROR(102, "accessToken错误"),

    ACCOUNT_ERROR(103, "用户名或密码错误,请重试！"),

    SIGN_ERROR(105, "签名错误"),

    AUTHORITY_ERROR(106, "权限错误"),

    USER_IS_DISABLED(107, "当前账号已暂停使用,详情请联系客服咨询"),

    RESUBMIT(202, "请勿重复提交"),

    DUPLICATE_KEY_ERROR(203, "信息已经存在,请不要重复添加"),

    FEIGN_SERVICE_ERROR(301, "远程服务异常"),

    ;

    private Integer code;

    private String message;

    private BErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
