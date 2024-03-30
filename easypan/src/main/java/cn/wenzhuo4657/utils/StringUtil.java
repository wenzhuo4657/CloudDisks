package cn.wenzhuo4657.utils;

import cn.wenzhuo4657.domain.enums.HttpeCode;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;


public class StringUtil {

    public static String getRandom_Number(Integer codeLength) {
        return RandomStringUtils.randomNumeric(codeLength);
    }
    /**
    * @Author wenzhuo4657
    * @Description
    * @Date 16:18 2024-03-17
    * @Param [str]
    * @return boolean  true表示str是一个不符合要求的string,false表示符合初步要求
    **/
    public static boolean isEmpty(String str) {
        if (null ==str||"".equals(str)||"null".equals(str)||"\u0000".equals(str)){
            return  true;
        }else if("".equals(str.trim())){
            return  true;
        }else{
            return  false;
        }
    }

    public static String getMd5(String password) {
        return isEmpty(password)? null: DigestUtils.md5Hex(password);
    }

    /**
    * @Author wenzhuo4657
    * @Description 对传入路径进行校验，保证路径为非空且不存在越级读取
    * @Date 12:01 2024-03-21
    * @Param [path]
    * @return boolean
    **/

    public  static  boolean pathIsOk(String path){
        if (StringUtil.isEmpty(path)){
            return  true;
        }
        if (path.contains("../")||path.contains("..\\")){
            return  false;
        }
        return  true;
    }

    /**
    * @Author wenzhuo4657
    * @Description 对文件进行重命名，每次调用，随机数字部分都会增长，
    * @Date 20:17 2024-03-26
    * @Param [filename]
    * @return java.lang.String
    **/

    public  static  String rename(String filename){
        String fileNameReal=getFileName_Suffix(filename);
        String suffix=getFileSuffix(filename);
        return  fileNameReal+"_"+getRandom_Number(HttpeCode.code_length)+suffix;
    }

    /**
    * @Author wenzhuo4657
    * @Description  返回文件后缀
    * @Date 10:47 2024-03-30
    * @Param [filename]
    * @return java.lang.String
    **/
    public static String getFileSuffix(String filename) {
        Integer index=filename.lastIndexOf(".");
        if (index==-1){
            return "";
        }
        String Suffix=filename.substring(index);
        return  Suffix;

    }


    /**
    * @Author wenzhuo4657
    * @Description 返回文件前缀
    * @Date 10:47 2024-03-30
    * @Param [filename]
    * @return java.lang.String
    **/

  public static String getFileName_Suffix(String filename) {
        Integer index=filename.lastIndexOf(".");
        if (index==-1){
            return  filename;
        }
        filename=filename.substring(0,index);
        return  filename;
    }




}
