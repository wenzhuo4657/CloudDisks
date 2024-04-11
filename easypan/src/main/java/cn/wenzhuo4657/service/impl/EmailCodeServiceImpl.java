package cn.wenzhuo4657.service.impl;

import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.enums.appconfig;
import cn.wenzhuo4657.domain.dto.SenderDtoDefault;
import cn.wenzhuo4657.domain.entity.EmailCode;
import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.EmailCodeMapper;
import cn.wenzhuo4657.mapper.UserInfoMapper;
import cn.wenzhuo4657.service.EmailCodeService;
import cn.wenzhuo4657.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import  cn.wenzhuo4657.config.redisComponent;
import javax.annotation.Resource;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * 邮箱验证码 表
(EmailCode)表服务实现类
 *
 * @author makejava
 * @since 2024-03-15 18:49:01
 */
@Service("emailCodeService")
public class EmailCodeServiceImpl extends ServiceImpl<EmailCodeMapper, EmailCode> implements EmailCodeService {

    private Logger logger= LoggerFactory.getLogger(EmailCodeServiceImpl.class);
    @Autowired
    private  EmailCodeMapper emailCodeMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Resource
    private JavaMailSender sender;

    @Resource
    private appconfig appConfig;

    @Resource
    private  redisComponent redisComponent;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendEmailcode(String email, Integer type) {
        if (type== HttpeCode.zero) {
            LambdaQueryWrapper<UserInfo> wrapper
                    =new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getEmail,email);
//            wrapper.eq(UserInfo::getStatus,HttpeCode.Status_Ok);
            UserInfo user=userInfoMapper.selectOne(wrapper);
            if (user!=null){
                throw new SystemException(HttpeCode.email_exits);
            }
        }


        String code= StringUtil.getRandom_Number(HttpeCode.code_length);//生成随机验证码
        sendEmail(email,code);

//        将数据库中同邮箱的所有验证码停用
        emailCodeMapper.disableEmailCode(email);


//

        EmailCode emailCode=new EmailCode();
        emailCode.setCode(code);
        emailCode.setEmail(email);
        emailCode.setStatus(HttpeCode.email_statusOne);
        emailCode.setCreatTime(new Date());
        emailCodeMapper.insert(emailCode);



    }

    private void sendEmail(String email, String code) {

        MimeMessage message=sender.createMimeMessage();
        MimeMessageHelper helper= null;
        try {
            helper = new MimeMessageHelper(message,true);
            helper.setFrom(appConfig.getUsername());
            helper.setTo(email);
            SenderDtoDefault senderDtoDefault=redisComponent.getSenderDtodefault();
            helper.setSubject(senderDtoDefault.getRegisterEmailTitle());
            helper.setText(String.format(senderDtoDefault.getRegisterEmailContent(),code));
            helper.setSentDate(new Date());
            sender.send(message);
        } catch (MessagingException e) {
            logger.info("{}",e);
            throw  new SystemException(HttpeCode.SendEmail_no_OK);
        }
    }




    @Override
    public  void  calibration(String email, String emailCode){
        LambdaQueryWrapper<EmailCode> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(EmailCode::getEmail,email);
        wrapper.eq(EmailCode::getStatus,1);
        EmailCode ema=emailCodeMapper.selectOne(wrapper);
        if (ema==null){
            throw  new SystemException("验证码未发送,请发送验证码");
        }
//        if (ema.getCode()!=emailCode||
//                System.currentTimeMillis()-ema.getCreatTime().getTime()> HttpeCode.Email_Length *60*1000){
//            throw new SystemException("验证码无效;可能原因有，过期、不正确");
//        }
        emailCodeMapper.disableEmailCode(email);
    }



}
