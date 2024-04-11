package cn.wenzhuo4657.domain.query;

import lombok.Data;

/**
 * @author: 35238
 * 功能:
 * 时间: 2023-12-16 20:47
 */
@Data
public class UserInfoQuery extends BaseParam {
    //用户ID
    private String userId;
    //昵称
    private String userName;
    //邮箱
    private String email;
    //密码
    private String password;
    //加入时间
    private String creatTime;
    //最后登录时间
    private String updateTime;
    //0:禁用 1:正常
    private Integer status;

    private String nickNameFuzzy;
    private String userIdFuzzy;
    private String emailFuzzy;
    private String qqAvatar;
    private String qqAvatarFuzzy;
    private String qqOpenId;
    private String qqOpenIdFuzzy;
    private String passwordFuzzy;
    private String joinTimeStart;
    private String joinTimeEnd;
    private String lastLoginTimeStart;
    private String lastLoginTimeEnd;
    private Long useSpace;
    private Long totalSpace;
}