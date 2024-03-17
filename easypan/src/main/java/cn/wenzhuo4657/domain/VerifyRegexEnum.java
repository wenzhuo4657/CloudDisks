package cn.wenzhuo4657.domain;

public enum VerifyRegexEnum {
    NO("","不校验"),
    EMALL("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$","邮箱"),
    PASSWORD("^[A-Za-z0-9!@#$%^&*(),.?\\\":{}|<>;+=_-]{0,10}$","只能是数字，字母，特殊字符，0-10位");

    private  String regex;
    private  String desc;

    VerifyRegexEnum(String regex, String desc) {
        this.regex = regex;
        this.desc = desc;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
