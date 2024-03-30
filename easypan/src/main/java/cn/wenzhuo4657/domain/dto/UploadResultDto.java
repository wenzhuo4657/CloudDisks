package cn.wenzhuo4657.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: UploadResultDto
 * @author: wenzhuo4657
 * @date: 2024/3/26 11:38
 * @Version: 1.0
 * @description:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UploadResultDto {
    private  String fileId;
    private  String status;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}