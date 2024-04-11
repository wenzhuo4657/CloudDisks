package cn.wenzhuo4657.mapper;

import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.domain.query.UserInfoQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * (UserInfo)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-15 14:18:10
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {


    UserInfo selectByNickName(String nickName);

    Integer updateUserSpace(String userId, Long fileSize);

    Integer selectCount(UserInfoQuery query);

    List<UserInfo> findListByInfoQuery(UserInfoQuery query);
}
