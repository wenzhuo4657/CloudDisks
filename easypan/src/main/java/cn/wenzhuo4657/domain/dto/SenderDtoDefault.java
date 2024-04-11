package cn.wenzhuo4657.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SenderDtoDefault implements Serializable{
    private  String registerEmailTitle="邮箱验证码";
    private  String registerEmailContent="你好，你的邮箱验证码是：%s.15分钟有效";
    private  String userInitUseSpace="5";

    public SenderDtoDefault() {
    }

    public SenderDtoDefault(String registerEmailTitle, String registerEmailContent, String userInitUserSpace) {
        this.registerEmailTitle = registerEmailTitle;
        this.registerEmailContent = registerEmailContent;
        this.userInitUseSpace = userInitUserSpace;
    }

    public String getRegisterEmailTitle() {
        return registerEmailTitle;
    }

    public void setRegisterEmailTitle(String registerEmailTitle) {
        this.registerEmailTitle = registerEmailTitle;
    }

    public String getRegisterEmailContent() {
        return registerEmailContent;
    }

    public void setRegisterEmailContent(String registerEmailContent) {
        this.registerEmailContent = registerEmailContent;
    }

    public String getUserInitUseSpace() {
        return userInitUseSpace;
    }

    public void setUserInitUseSpace(String userInitUseSpace) {
        this.userInitUseSpace = userInitUseSpace;
    }

    @Override
    public String toString() {
        return "SenderDtoDefault{" +
                "registerEmailTitle='" + registerEmailTitle + '\'' +
                ", registerEmailContent='" + registerEmailContent + '\'' +
                ", userInitUseSpace='" + userInitUseSpace + '\'' +
                '}';
    }
}
