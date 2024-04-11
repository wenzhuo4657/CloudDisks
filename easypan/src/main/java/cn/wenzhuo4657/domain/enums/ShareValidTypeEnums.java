package cn.wenzhuo4657.domain.enums;

/**
 * @author: 35238
 * 功能: 分享码的有效期
 * 时间: 2023-12-15 17:13
 */
public enum ShareValidTypeEnums {
    DAY_1(0, 1, "1天"),
    DAY_7(1, 7, "7天"),
    DAY_30(2, 30, "30天"),
    FOREVER(3, -1, "永久有效");

    //存入数据库的参数
    private Integer type;
    //根据类型判断天数
    private Integer days;
    //枚举的参数描述
    private String desc;

    ShareValidTypeEnums(Integer type, Integer days, String desc) {
        this.type = type;
        this.days = days;
        this.desc = desc;
    }
    public static ShareValidTypeEnums getByType(Integer type) {
        for (ShareValidTypeEnums typeEnums : ShareValidTypeEnums.values()) {
            if (typeEnums.getType().equals(type)) {
                return typeEnums;
            }
        }
        return null;
    }
    public Integer getType() {
        return type;
    }
    public Integer getDays() {
        return days;
    }
    public String getDesc() {
        return desc;
    }
}