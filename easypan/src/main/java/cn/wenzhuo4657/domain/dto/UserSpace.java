package cn.wenzhuo4657.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @className: UserSpace
 * @author: wenzhuo4657
 * @date: 2024/3/19 12:04
 * @Version: 1.0
 * @description:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSpace implements Serializable {
    private long usespace;
    private  long totalSpace;

    public long getUsespace() {
        return usespace;
    }

    public void setUsespace(long usespace) {
        this.usespace = usespace;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }


    @Override
    public String toString() {
        return "UserSpace{" +
                "usespace=" + usespace +
                ", totalSpace=" + totalSpace +
                '}';
    }
}