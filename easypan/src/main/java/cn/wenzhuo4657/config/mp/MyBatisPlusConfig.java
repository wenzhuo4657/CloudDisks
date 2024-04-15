//package cn.wenzhuo4657.config.mp;
//
//import cn.wenzhuo4657.Handle.mybatisplus.MyMetaObjectHandler;
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.*;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.boot.web.servlet.ServletComponentScan;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @className: MyBatisPlusConfig
// * @author: wenzhuo4657
// * @date: 2024/4/14 19:23
// * @Version: 1.0
// * @description:
// */
//@Configuration
//@MapperScan("cn.wenzhuo4657.mapper")
//@ServletComponentScan
//public class MyBatisPlusConfig {
//    @Bean
//    public MyMetaObjectHandler myMetaObjectHandler() {
//        return new MyMetaObjectHandler();
//    }
//
//    @Bean
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        // 分页插件
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        // 字段填充插件
//        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
//        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor());
//        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
//        // 自定义插件
//        interceptor.addInnerInterceptor((InnerInterceptor) new MyMetaObjectHandler());
//        return interceptor;
//    }
//}