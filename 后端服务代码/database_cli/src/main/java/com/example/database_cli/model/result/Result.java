package com.example.database_cli.model.result;
import com.example.database_cli.enums.ResultEnum;
import lombok.Data;

@Data
public class Result<T> {
    public int code;
    public String msg;
    public T data;

    public Result() {}

    public Result(String msg, int code, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result success(){
        return new Result(ResultEnum.SUCCESS.getMsg(), ResultEnum.SUCCESS.getCode(), null);
    }

    public static <T> Result success(T data){
        return new Result(ResultEnum.SUCCESS.getMsg(), ResultEnum.SUCCESS.getCode(), data);
    }

    public static <T> Result fail(){
        return new Result(ResultEnum.ERROR_UNKNOWN.getMsg(), ResultEnum.ERROR_UNKNOWN.getCode(), null);
    }

    public static <T> Result fail(ResultEnum resultEnum){
        return new Result(resultEnum.getMsg(), resultEnum.getCode(), null);
    }

    public static <T> Result success(T data,int code, String msg) {
        Result<T> r = new Result<>( msg,code,data);
        return r;
    }

}