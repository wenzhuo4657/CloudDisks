package cn.wenzhuo4657.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Mapping
public @interface Global_interceptor {
    //是否在某个方法中开启参数校验，此注解针对的是方法，表示是否开始搜寻含有VerifyParam注解的参数，并对其使用相应校验方法
    boolean checkparams() default false;

    /**
     * 校验登录
     */
    boolean checkLogin() default true;

    /**
     * 校验admin
     */
    boolean checkAdmin() default false;
}