package cn.wenzhuo4657.domain.enums;

public enum UserStatusEnum {
    disable(0,"禁用"),normal(1,"正常");


    Integer status;
    String desc;

    UserStatusEnum() {
    }

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
