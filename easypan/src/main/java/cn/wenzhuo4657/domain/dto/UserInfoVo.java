package cn.wenzhuo4657.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: 35238
 * 功能:
 * 时间: 2023-12-16 20:52
 */
@Data
public class UserInfoVo implements Serializable {
    //用户ID
    private String userId;
    //昵称
    @JsonProperty(value = "nickName")
    private String userName;
    //邮箱
    private String email;
    //qq头像
    private String qqAvatar;

    //加入时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "joinTime")
    private Date creatTime;

    //最后登录时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "lastLoginTime")
    private Date updateTime;

    //0:禁用 1:正常
    private Integer status;
    //使用空间单位byte
    private Long useSpace;
    //总空间单位byte
    private Long totalSpace;
}