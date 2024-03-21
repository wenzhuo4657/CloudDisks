package cn.wenzhuo4657.service;

import cn.wenzhuo4657.domain.entity.EmailCode;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 邮箱验证码 表
(EmailCode)表服务接口
 *
 * @author makejava
 * @since 2024-03-15 18:49:01
 */
public interface EmailCodeService extends IService<EmailCode> {

    void sendEmailcode(String email, Integer type);

    void calibration(String email, String emailCode);


}
