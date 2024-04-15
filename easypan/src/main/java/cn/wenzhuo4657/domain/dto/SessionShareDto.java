package cn.wenzhuo4657.domain.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author: 35238
 * 功能:
 * 时间: 2023-12-18 21:38
 */
@Data
public class SessionShareDto {
    private String shareId;
    private String shareUserId;
    private Date expireTime;
    private String fileId;
}