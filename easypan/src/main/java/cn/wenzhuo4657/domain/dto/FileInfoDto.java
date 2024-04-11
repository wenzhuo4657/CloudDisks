package cn.wenzhuo4657.domain.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**1
 * @author: 35238
 * 功能:
 * 时间: 2023-12-06 18:02
 */
@Data
public class FileInfoDto implements Serializable {

    /**
     * 文件ID
     */
    @TableId
    private String fileId;

    /**
     * 父级ID
     */
    private String filePid;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 封面
     */
    private String fileCover;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recoveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")//保证json转化时的正确
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")//保证mvc请求中格式的正确
    private Date lastUpdateTime;

    /**
     * 0:文件 1:目录
     */
    private Integer folderType;

    /**
     * 1:视频 2:音频  3:图片 4:文档 5:其他
     */
    private Integer fileCategory;

    /**
     * 1:视频 2:音频  3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他
     */
    private Integer fileType;

    /**
     * 0:转码中 1转码失败 2:转码成功
     */
    private Integer status;

    private String nickName;
    private String userId;
}