package cn.wenzhuo4657.annotation;


import cn.wenzhuo4657.domain.enums.VerifyRegexEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER,ElementType.FIELD})
public @interface VerifyParam {
    int min() default  -1;
    int max() default  -1;
    boolean required() default  false;//是否在Global_interceptor注解所标注的方法的参数中开启校验，此注解针对的是参数或者自定义类中的属性
    VerifyRegexEnum regex() default  VerifyRegexEnum.NO;//菜单类，标注注解时，指定使用的正则表达式
}
