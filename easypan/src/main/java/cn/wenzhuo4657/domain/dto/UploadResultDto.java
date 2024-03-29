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
public class UploadResultDto implements Serializable {
    private  String fileid;
    private  String status;

    public String getFileid() {
        return fileid;
    }

    public void setFileid(String fileid) {
        this.fileid = fileid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}