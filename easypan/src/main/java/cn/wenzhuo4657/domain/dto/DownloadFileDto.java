package cn.wenzhuo4657.domain.dto;

import lombok.Data;

/**
 * @author: 35238
 * 功能: 文件下载
 * 时间: 2023-12-14 15:02
 */
@Data
public class DownloadFileDto {
    private String downloadCode;
    private String fileId;
    private String fileName;
    private String filePath;
}