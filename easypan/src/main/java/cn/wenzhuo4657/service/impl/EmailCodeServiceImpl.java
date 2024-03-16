package cn.wenzhuo4657.service.impl;

import cn.wenzhuo4657.domain.HttpeCode;
import cn.wenzhuo4657.domain.entity.EmailCode;
import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.EmailCodeMapper;
import cn.wenzhuo4657.mapper.UserInfoMapper;
import cn.wenzhuo4657.service.EmailCodeService;
import cn.wenzhuo4657.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Autowired
    private  EmailCodeMapper emailCodeMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
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

//        将数据库中同邮箱的验证码停用
        emailCodeMapper.disableEmailCode(email);

        EmailCode emailCode=new EmailCode();
        emailCode.setCode(code);
        emailCode.setEmail(email);
        emailCode.setStatus(HttpeCode.zero);
        emailCode.setCreatTime(new Date());
        emailCodeMapper.insert(emailCode);



    }
}
