package cn.wenzhuo4657.domain.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: UserSpace
 * @author: wenzhuo4657
 * @date: 2024/3/19 12:04
 * @Version: 1.0
 * @description:
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSpace implements Serializable{
    @JSONField(name = "useSpace")
    private long useSpace;
    private  long totalSpace;


    public long getUseSpace() {
        return useSpace;
    }

    public void setUseSpace(long useSpace) {
        this.useSpace = useSpace;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }


}