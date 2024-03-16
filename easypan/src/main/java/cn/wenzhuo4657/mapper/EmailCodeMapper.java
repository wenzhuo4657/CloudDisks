package cn.wenzhuo4657.mapper;

import cn.wenzhuo4657.domain.entity.EmailCode;
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


    void disableEmailCode(String email);
}
