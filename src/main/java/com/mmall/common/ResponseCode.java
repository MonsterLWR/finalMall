package com.mmall.common;

/**
 * 功能描述: 配合服务器响应类使用的枚举类
 *
 * @auther: Lee
 * @date: 2018/9/16 10:43
 */
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    ILLEGAL_ARGUMENT(2, "ILLEGAL_ARGUMENT"),
    NEED_LOGIN(10, "NEED_LOGIN");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
