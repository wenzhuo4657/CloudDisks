package cn.wenzhuo4657.service.impl;

import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.mapper.UserInfoMapper;
import cn.wenzhuo4657.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * (UserInfo)表服务实现类
 *
 * @author makejava
 * @since 2024-03-15 14:18:12
 */
@Service("userInfoService")
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
