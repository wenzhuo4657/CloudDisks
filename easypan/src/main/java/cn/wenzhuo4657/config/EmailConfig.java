package cn.wenzhuo4657.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 设置基本邮件发送配置
        mailSender.setHost(host);
        mailSender.setPort(port);

        // 配置SMTP协议的SSL属性
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.debug", "true");
        javaMailProperties.setProperty("mail.smtp.timeout", "200000");
        javaMailProperties.setProperty("mail.smtp.port", Integer.toString(port));
        javaMailProperties.setProperty("mail.smtp.socketFactory.port", "465");
        javaMailProperties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.setProperty("mail.smtp.socketFactory.fallback", "false");

        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setJavaMailProperties(javaMailProperties);

        return mailSender;
    }
}