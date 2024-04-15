package cn.wenzhuo4657.Handle.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //  wenzhuo TODO 2024/3/30 : 自动填充未生效
    @Override
    //只要对数据库执行了插入语句，那么就会执行到这个方法
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("creatTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}