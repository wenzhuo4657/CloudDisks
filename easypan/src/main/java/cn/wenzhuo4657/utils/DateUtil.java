package cn.wenzhuo4657.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import  java.util.Date;

/**
 * @className: DateUtil
 * @author: wenzhuo4657
 * @date: 2024/3/28 19:22
 * @Version: 1.0
 * @description:
 */
public class DateUtil {
    private  static  final  Object lockobj=new Object();
    private  static Map<String,ThreadLocal<SimpleDateFormat>>
          dateFormat=new HashMap<>();
    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = dateFormat.get(pattern);
        if (tl == null) {
            synchronized (lockobj) {
                tl =dateFormat.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    dateFormat.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            return getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    //根据给定的整数参数 `day`，获取当前日期之后 `day` 天的日期。
    public static Date getAfterDate(Integer day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

}