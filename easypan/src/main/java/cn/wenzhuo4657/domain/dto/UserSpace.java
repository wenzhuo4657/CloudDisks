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

public class UserSpace {
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


    @Override
    public String toString() {
        return "UserSpace{" +
                "useSpace=" + useSpace +
                ", totalSpace=" + totalSpace +
                '}';
    }
}