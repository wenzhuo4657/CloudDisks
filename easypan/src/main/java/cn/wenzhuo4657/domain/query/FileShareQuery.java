package cn.wenzhuo4657.domain.query;

import lombok.Data;

/**
 * @author: 35238
 * 功能:
 * 时间: 2023-12-15 15:57
 */

public class FileShareQuery extends BaseParam {
    //分享ID
    private String shareId;
    //文件ID
    private String fileId;
    //分享人ID
    private String userId;
    //有效期类型 0一天 1七天 2三十天 3永久有效
    private Integer validType;
    //失效时间
    private String expireTime;
    //分享时间
    private String shareTime;
    //提取码
    private String code;
    //浏览次数
    private Integer showCount;
    //是否要查询文件名
    private Boolean queryFileName;
    
    private String shareIdFuzzy;
    private String fileIdFuzzy;
    private String userIdFuzzy;
    private String expireTimeStart;
    private String expireTimeEnd;
    private String shareTimeStart;
    private String shareTimeEnd;
    private String codeFuzzy;


    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getValidType() {
        return validType;
    }

    public void setValidType(Integer validType) {
        this.validType = validType;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getShareTime() {
        return shareTime;
    }

    public void setShareTime(String shareTime) {
        this.shareTime = shareTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getShowCount() {
        return showCount;
    }

    public void setShowCount(Integer showCount) {
        this.showCount = showCount;
    }

    public Boolean getQueryFileName() {
        return queryFileName;
    }

    public void setQueryFileName(Boolean queryFileName) {
        this.queryFileName = queryFileName;
    }

    public String getShareIdFuzzy() {
        return shareIdFuzzy;
    }

    public void setShareIdFuzzy(String shareIdFuzzy) {
        this.shareIdFuzzy = shareIdFuzzy;
    }

    public String getFileIdFuzzy() {
        return fileIdFuzzy;
    }

    public void setFileIdFuzzy(String fileIdFuzzy) {
        this.fileIdFuzzy = fileIdFuzzy;
    }

    public String getUserIdFuzzy() {
        return userIdFuzzy;
    }

    public void setUserIdFuzzy(String userIdFuzzy) {
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getExpireTimeStart() {
        return expireTimeStart;
    }

    public void setExpireTimeStart(String expireTimeStart) {
        this.expireTimeStart = expireTimeStart;
    }

    public String getExpireTimeEnd() {
        return expireTimeEnd;
    }

    public void setExpireTimeEnd(String expireTimeEnd) {
        this.expireTimeEnd = expireTimeEnd;
    }

    public String getShareTimeStart() {
        return shareTimeStart;
    }

    public void setShareTimeStart(String shareTimeStart) {
        this.shareTimeStart = shareTimeStart;
    }

    public String getShareTimeEnd() {
        return shareTimeEnd;
    }

    public void setShareTimeEnd(String shareTimeEnd) {
        this.shareTimeEnd = shareTimeEnd;
    }

    public String getCodeFuzzy() {
        return codeFuzzy;
    }

    public void setCodeFuzzy(String codeFuzzy) {
        this.codeFuzzy = codeFuzzy;
    }
}