package cn.wenzhuo4657.domain.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 邮箱验证码 表
(EmailCode)表实体类
 *
 * @author makejava
 * @since 2024-03-15 18:49:00
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("email_code")
public class EmailCode  {
    @TableId
    private String email;
    //编号@TableId
    private String code;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date creatTime;
    //0:使用；1未使用
    private Integer status;
    
}
