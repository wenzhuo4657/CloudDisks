package cn.wenzhuo4657.domain.enums;

public enum FileCategoryEnums {
    VIDEO(1,"video","视频"),
    MUSIC(2,"music","音频"),
    IMAGE(3,"image","图片"),
    DOC(4,"doc","文档"),
    OTHERS(5,"others","其他");


    private  Integer category;
    private  String code;
    private  String desc;

    FileCategoryEnums(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
        this.desc = desc;
    }


    /**
    * @Author wenzhuo4657
    * @Description  根据状态码获取对应菜单
    * @Date 19:01 2024-03-23
    * @Param [code]
    * @return cn.wenzhuo4657.domain.enums.FileCategoryEnums
    **/

    public  static  FileCategoryEnums getByCode(String code){
        for (FileCategoryEnums item:FileCategoryEnums.values()){
            if (item.getCode().equals(code)){
                return  item;
            }
        }
        return  null;
    }

    public Integer getCategory() {
        return category;
    }


    private String getCode() {
        return  this.code;
    }
}
