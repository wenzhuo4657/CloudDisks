package cn.wenzhuo4657.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: 35238
 * 功能:
 * 时间: 2023-12-18 20:51
 */
@Data
public class ShareInfoVo {
    //分享时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date shareTime;

    //失效时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    //分享人的昵称
    private String nickName;
    //文件名
    private String fileName;
    //当前用户
    private Boolean currentUser;
    //文件ID
    private String fileId;
    //用户ID
    private String userId;
    //头像
    private String avatar;
}