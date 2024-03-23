package cn.wenzhuo4657.domain.enums;

public enum ResponseEnum {

    CODE_200(200,"请求成功"),
    CODE_404(404,"请求地址不存在"),
    CODE_600(600,"请求参数错误"),
    CODE_601(601,"信息已存在"),
    CODE_500(500,"服务器返回错误，请联系管理员"),
    CODE_901(901,"登录超时,请重新登录"), CODE_902(902,"权限不足" );
    private  Integer code;
    private String msg;

    ResponseEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public Integer getCode() {
        return code;
    }



    public String getMsg() {
        return msg;
    }


}
