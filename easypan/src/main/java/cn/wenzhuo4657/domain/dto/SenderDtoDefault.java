package cn.wenzhuo4657.domain.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SenderDtoDefault implements Serializable {
    private  String Mail_Title="邮箱验证码";
    private  String Mail_text="你好，你的邮箱验证码是：%s.15分钟有效";
    private  Integer userInitUserSpace=5;

    public String getMail_Title() {
        return Mail_Title;
    }

    public void setMail_Title(String mail_Title) {
        Mail_Title = mail_Title;
    }

    public String getMail_text() {
        return Mail_text;
    }

    public void setMail_text(String mail_text) {
        Mail_text = mail_text;
    }

    public Integer getUserInitUserSpace() {
        return userInitUserSpace;
    }

    public void setUserInitUserSpace(Integer userInitUserSpace) {
        this.userInitUserSpace = userInitUserSpace;
    }
}
