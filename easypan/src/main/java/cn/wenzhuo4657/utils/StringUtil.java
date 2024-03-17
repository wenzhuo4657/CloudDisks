package cn.wenzhuo4657.utils;

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
}
