package com.example.database_cli.enums;

public enum ResultEnum {

    SUCCESS(200, "请求成功"),
    ERROR_UNKNOWN(500,"未知错误"),
    ERROR_NOPARAMETERS(10002, "参数为空"),
    ERROR_NOTFOUND(10003,"资源未找到"),
    ERROR_BADPARMETERS(10001, "参数类型错误"),
    ERROR_OPERATION(400, "操作不成功"),
    ERROR_NAMEDUPLICATION(401, "用户名已存在"),
            ;

    int code;
    String msg;
    ResultEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

}
