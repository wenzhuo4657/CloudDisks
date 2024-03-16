package cn.wenzhuo4657.domain.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * (UserInfo)表实体类
 *
 * @author makejava
 * @since 2024-03-15 14:18:11
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_info")
public class UserInfo  {
    //用户id@TableId
    private String userId;

    
    private String userName;
    
    private String email;
    
    private String qqOpenId;
    
    private String qqAvatar;
    
    private String password;
    
    private Date creatTime;
    
    private Date updateTime;
    //用户状态，0表禁用，1表正常
    private Integer status;
    //使用的网盘大小，单位byte
    private Long userSpace;
    //可使用的网盘总量
    private Long totalSpace;
    
}
