package cn.wenzhuo4657;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@EnableAsync//开启异步
@SpringBootApplication(scanBasePackages = "cn.wenzhuo4657")
@EnableTransactionManagement//开启事务
@EnableScheduling//开启job任务
@MapperScan("cn.wenzhuo4657.mapper")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class,args);
    }
}