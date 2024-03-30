package cn.wenzhuo4657.Handle.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 35238
 * @date 2023/7/26 0026 20:52
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //  wenzhuo TODO 2024/3/30 : 自动填充未生效
    @Override
    //只要对数据库执行了插入语句，那么就会执行到这个方法
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("create_time", new Date(), metaObject);
        this.setFieldValByName("update_time", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("update_time", new Date(), metaObject);
    }
}