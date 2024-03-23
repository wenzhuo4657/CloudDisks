package cn.wenzhuo4657.domain.enums;

/**
 * @className: FileDefalgEnums
 * @author: wenzhuo4657
 * @date: 2024/3/23 18:05
 * @Version: 1.0
 * @description:
 */
public enum FileDefalgEnums {
    DEL(0,"删除"),
    RECYCLE(1,"回收站"),
    USING(2,"使用中");
    private  Integer status;
    private  String desc;


    FileDefalgEnums(Integer status, String desc) {
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