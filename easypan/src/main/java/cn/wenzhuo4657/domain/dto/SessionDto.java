package cn.wenzhuo4657.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @className: SessionDto
 * @author: wenzhuo4657
 * @date: 2024/3/19 11:37
 * @Version: 1.0
 * @description:
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionDto implements Serializable  {
    private  String nickName;
    private  String userId;
    private  Boolean isAdmin;
    private String avatar;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "SessionDto{" +
                "nickName='" + nickName + '\'' +
                ", userId='" + userId + '\'' +
                ", isAdmin=" + isAdmin +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}