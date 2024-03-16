package cn.wenzhuo4657.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class StringUtil {

    public static String getRandom_Number(Integer codeLength) {
        return RandomStringUtils.randomNumeric(codeLength);
    }
}
