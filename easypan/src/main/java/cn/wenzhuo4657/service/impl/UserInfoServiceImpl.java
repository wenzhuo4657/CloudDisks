package cn.wenzhuo4657.service.impl;


import cn.wenzhuo4657.config.RedisCache;
import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.enums.appconfig;
import cn.wenzhuo4657.domain.dto.SenderDtoDefault;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.dto.UserSpace;
import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.mapper.UserInfoMapper;
import cn.wenzhuo4657.service.EmailCodeService;
import cn.wenzhuo4657.service.UserInfoService;
import cn.wenzhuo4657.utils.StringUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.wenzhuo4657.config.redisComponent;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * (UserInfo)表服务实现类
 *
 * @author makejava
 * @since 2024-03-15 14:18:12
 */
@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private  UserInfoMapper mapper;
    @Resource
    private EmailCodeService emailCodeService;
    @Autowired
    private  redisComponent redisComponent;
    @Resource
    private RedisCache redisConfig;

    @Autowired
    private appconfig appconfig;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String email, String nickName, String password, String emailCode) {

        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getEmail,email);
        UserInfo userInfo=mapper.selectOne(wrapper);
        if(userInfo!=null){
            throw new SystemException("该邮箱已注册");
        }
        userInfo=mapper.selectByNickName(nickName);
        if(userInfo!=null){
            throw  new SystemException("该昵称已存在");
        }

        //校验邮箱验证码，并停用验证码，
        emailCodeService.calibration(email,emailCode);

        //验证通过在数据库中进行注册
        String userid= StringUtil.getRandom_Number(HttpeCode.userId_length);
        UserInfo new_user=new UserInfo();
        new_user.setUserId(userid);
        new_user.setUserName(nickName);
        new_user.setEmail(email);
        new_user.setPassword(StringUtil.getMd5(password));

        SenderDtoDefault senderDtoDefault=redisComponent.getSenderDtodefault();
        new_user.setTotalSpace(senderDtoDefault.getUserInitUserSpace().longValue()*HttpeCode.MB);
        new_user.setUserSpace(0L);
        this.mapper.insert(new_user);
    }
    @Resource
    private FileInfoMapper fileInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionDto login(String email, String password) {
        //1，验证账户并更新登录时间
        LambdaQueryWrapper<UserInfo> wrapper1=new LambdaQueryWrapper<>();
        wrapper1.eq(UserInfo::getEmail,email);
        UserInfo userInfo=mapper.selectOne(wrapper1);
        if (null==userInfo||!userInfo.getPassword().equals(password)){
            throw  new SystemException("账号或者密码错误");
        }

        if (userInfo.getStatus()==HttpeCode.email_status_No) {
            throw  new SystemException("账号已停用");
        }
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getEmail,email);
        mapper.update(new UserInfo(),wrapper);


        SessionDto sessionDto=new SessionDto();
        sessionDto.setUserId(userInfo.getUserId());
        sessionDto.setNickName(userInfo.getUserName());
        if (ArrayUtils.contains(appconfig.getAdmin().split(","),email)){
            sessionDto.setAdmin(true);
        }else{
            sessionDto.setAdmin(false);
        }
        UserSpace userSpace=new UserSpace();
        userSpace.setUseSpace(fileInfoMapper.selectAllByUserIdLong(sessionDto.getUserId()));
        userSpace.setTotalSpace(userInfo.getTotalSpace());
        redisComponent.saveUserid_space(userInfo.getUserId(),userSpace);
        return sessionDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String emailCode) {
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getEmail,email);
        UserInfo userInfo=mapper.selectOne(wrapper);

        if(null==userInfo){
            throw  new SystemException("邮箱账号不存在");
        }

//校验验证码
        emailCodeService.calibration(email,emailCode);

        //修改密码，写入数据库中
        UserInfo userInfo1=new UserInfo();
        userInfo1.setPassword(StringUtil.getMd5(password));
        update(userInfo1,wrapper);

    }

}
