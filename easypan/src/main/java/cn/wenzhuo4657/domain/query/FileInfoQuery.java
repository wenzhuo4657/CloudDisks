package cn.wenzhuo4657.domain.query;

import lombok.Data;

/**
 * @author: 35238
 * 功能: 文件信息参数
 * 时间: 2023-12-06 16:39
 */
@Data
public class FileInfoQuery extends BaseParam{

    private Integer pageNo;//当前页
    private Integer pageSize;//页大小
    private String orderBy;

    /**
     * 文件ID
     */
    private String fileId;
    private String fileIdFuzzy;

    /**
     * 用户ID
     */
    private String userId;
    private String userIdFuzzy;

    /**
     * 文件MD5值
     */
    private String fileMd5;
    private String fileMd5Fuzzy;

    /**
     * 父级ID
     */
    private String filePid;
    private String filePidFuzzy;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String fileName;
    private String fileNameFuzzy;

    /**
     * 封面
     */
    private String fileCover;
    private String fileCoverFuzzy;

    /**
     * 文件路径
     */
    private String filePath;
    private String filePathFuzzy;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 最后更新时间
     */
    private String lastUpdateTime;

    private String lastUpdateTimeStart;

    private String lastUpdateTimeEnd;

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

    /**
     * 回收站时间
     */
    private String recoveryTime;
    private String recoveryTimeStart;
    private String recoveryTimeEnd;

    /**
     * 删除标记 0:删除  1:回收站  2:正常
     */
    private Integer delFlag;

    /**
     * 文件夹id集合，
     */
    private String[] fileIdArray;
    private String[] filePidArray;
    private String[] excludeFileIdArray;
    private Boolean queryExpire;
    private Boolean queryNickName;
}