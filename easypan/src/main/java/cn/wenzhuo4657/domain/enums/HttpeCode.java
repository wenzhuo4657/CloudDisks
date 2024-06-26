package cn.wenzhuo4657.domain.enums;


import lombok.Data;


public enum HttpeCode {

    Image_no_OK(505,"图片验证码不正确"),
    email_exits(506, "邮箱已存在"),
    SendEmail_no_OK(507, "邮件发送失败"),
    params_No_Ok(508,"全局拦截器方法中，检测出不符合要求的str" ),
    UPLOAD_FOLDER_NOT_EXIT(509, "转码文件不存在"),
    UPLOAD_MERGE_ONE_FAIL(510, "转码失败"),
    UPLOAD_MERGE_FILE_FAIL(511, "关闭流失败");
    public static final Integer email_statusOne=1;
    public static final Integer  code_length=5;
    public static Integer code_length_50=50;
    public static final Integer Status_Ok=1;//状态码是通用的，所以这里不做区分
    public static final Integer Status_NO_Ok=0;

    public  static  final  String redis_Mail_Key="redis_Mail_Key";
    public static final int Email_Length=15;
    public static final long MB =1024*1024;
    public static final Object TS_NAME ="index.ts" ;
    public static final String M3U8_NAME = "index.m3u8";


    public static Integer userId_length=10;
    public static Integer email_status_No=0;
    public static String redis_total="redis_total";
    public static String redis_userid_space="userid_space";
    public static String SessionDto_key="存入浏览器的登录凭证";
    public static Integer zero=0;
    public static String File_Folder_root="/file";
    public static String File_Folder_AvvatarName="/avater";
    public static String LoadDataList_File_sort="last_update_time";
    public static String Tempfolder_UploadFile="/temp/uploadFile";
    public static String redis_TempSize="TempSize";
    public static String tempFile="tempFile";
    public static String File_userid="File_userid";
    public static String UPLOAD_IO_CLOSE_FAIL="关闭流失败";
    public static String cmd_order="cmd命令执行失败，工具类ProcessUtils ";
    public static int LENGTH_150=150;
    public static String video="VIDEO";
    public static String img_png=".png";;
    public static Integer code_length_10=10;
    public static String Check_Type_0="0";
    public static String Check_Type_1="1";
    public static String redis_downloadDto="redis_downloadDto";
    public static String LoadRecycleList_File_sort="remove_time";
    public static Integer code_length_20=20;
    public static String SessionShareDto_Key="session_share_key_";
    public static String zero_str="0";


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



    public String getMsg() {
        return msg;
    }


}
