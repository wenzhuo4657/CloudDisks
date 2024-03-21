package cn.wenzhuo4657.utils;

import cn.wenzhuo4657.domain.VerifyRegexEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @className: VerifUtils
 * @author: wenzhuo4657
 * @date: 2024/3/17 16:35
 * @Version: 1.0
 * @description:
 */
public class VerifUtils {

    /**
    * @Author wenzhuo4657
    * @Description
    * @Date 16:39 2024-03-17
    * @Param [regex, res]
     * regex表示正则表达式， res表示将要校验的参数值
    * @return boolean true表示通过校验，false表示没通过
    **/
    public static boolean verify(String regex, String res) {
        if (StringUtil.isEmpty(res)){
            return  false;
        }
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(res);
        return  !matcher.matches();
    }

    public  static  boolean verify(VerifyRegexEnum regex,String res){
        return  verify(regex.getRegex(),res);
    }
}