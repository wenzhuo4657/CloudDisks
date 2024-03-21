package cn.wenzhuo4657.mapper;

import cn.wenzhuo4657.domain.entity.EmailCode;
import cn.wenzhuo4657.domain.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * 邮箱验证码 表
(EmailCode)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-15 18:48:59
 */
@Mapper
public interface EmailCodeMapper extends BaseMapper<EmailCode> {

/**
* @Author wenzhuo4657
* @Description 停用邮箱中所有验证码
* @Date 21:09 2024-03-18
* @Param [email]
* @return void
**/
    void disableEmailCode(String email);

    EmailCode selectByEmail(String email);
}
