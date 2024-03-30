package cn.wenzhuo4657.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: UserSpace
 * @author: wenzhuo4657
 * @date: 2024/3/19 12:04
 * @Version: 1.0
 * @description:
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSpace implements Serializable{
    private long useSpace;
    private  long totalSpace;

    public long getUsespace() {
        return useSpace;
    }

    public void setUsespace(long usespace) {
        this.useSpace = usespace;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }


}