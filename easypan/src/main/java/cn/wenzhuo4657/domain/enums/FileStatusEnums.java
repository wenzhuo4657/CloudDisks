package cn.wenzhuo4657.domain.enums;


/**
 * @author: 35238
 * 功能: 文件状态
 * 时间: 2023-12-09 14:35
 */
public enum FileStatusEnums {
    TRANSFER(0, "转码中"),
    TRANSFER_FAIL(1, "转码失败"),
    USING(2, "使用中");

    private Integer status;
    private String desc;

    FileStatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
