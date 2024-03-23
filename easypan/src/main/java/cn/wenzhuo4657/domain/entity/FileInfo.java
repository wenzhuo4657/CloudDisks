package cn.wenzhuo4657.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 文件信息(FileInfo)表实体类
 *
 * @author makejava
 * @since 2024-03-22 20:15:02
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("file_info")
public class FileInfo {
    @TableId
    private String fileId;

    private String userId;

    
    private String fileMd5;
    
    private String filePid;
    
    private Long fileSize;
    
    private String fileName;
    
    private String fileCover;
    
    private String filePath;
    
    private Date createTime;
    
    private Date lastUpdateTime;
    
    private Integer folderType;
    
    private Integer fileCategory;
    
    private Integer fileType;
    //0:未转码，1：转码失败，2：转码成功
    private Integer status;
    //0:删除，1：回收站，2：正常
    private Integer delFlag;
    //进入回收站的时间
    private Date removeTime;
    
}
