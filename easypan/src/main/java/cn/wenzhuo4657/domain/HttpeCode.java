package cn.wenzhuo4657.domain;


import lombok.Data;


public enum HttpeCode {
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "出现错误"),
    Image_no_OK(505,"图片验证码不正确"),
    email_exits(506, "邮箱已存在"),
    SendEmail_no_OK(507, "邮件发送失败"),
    params_No_Ok(508,"全局拦截器方法中，检测出不符合要求的str" );
    public static final Integer email_statusOne=1;
    public static final Integer  code_length=5;
    public static final Integer Status_Ok=1;//状态码是通用的，所以这里不做区分
    public static final Integer Status_NO_Ok=0;

    public  static  final  String redis_Mail_Key="redis_Mail_Key";
    public static final int Email_Length=15;
    public static final long MB =1024*1024;

    public static Integer userId_length=10;
    public static Integer email_status_No=0;
    public static String redis_total="redis_total";
    public static String redis_userid_space="userid_space";
    public static String SessionDto_key="存入浏览器的登录凭证";
    public static Integer zero=0;
    public static String File_Folder_root="/file";
    public static String File_Folder_AvvatarName="/avater";

    int code;
    String msg;
    HttpeCode(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }


    public static String Check_Ok="0";
    public static String Check_NO_Ok="1";


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
