package cn.wenzhuo4657.exception;

import cn.wenzhuo4657.domain.HttpeCode;


public class SystemException extends  RuntimeException{

    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SystemException(HttpeCode httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        //把某个枚举类里面的code和msg赋值给异常对象
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

    public SystemException( String msg) {
        super(msg);
        this.msg = msg;
    }
}
