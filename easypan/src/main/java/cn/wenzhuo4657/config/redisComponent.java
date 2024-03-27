package cn.wenzhuo4657.config;

import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.dto.SenderDtoDefault;
import cn.wenzhuo4657.domain.dto.UserSpace;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class redisComponent {
    @Resource
    private RedisCache redisConfig;

    public SenderDtoDefault getSenderDtodefault(){
        SenderDtoDefault senderDtoDefault= (SenderDtoDefault) redisConfig.getCacheObject(HttpeCode.redis_Mail_Key);
        if (null==senderDtoDefault){
            senderDtoDefault=new SenderDtoDefault();
            redisConfig.setCacheObject(HttpeCode.redis_Mail_Key,senderDtoDefault);
        }
        return  senderDtoDefault;
    }
    @Resource
    private FileInfoMapper fileInfoMapper;
    public void saveUserid_space(String userID, UserSpace space){
        //  wenzhuo TODO 2024/3/27 : 时间颗粒度和时间的使用，此处暂且不改变键值对的生命周期
        redisConfig.setCacheObject(HttpeCode.redis_userid_space+userID,space);

    }

    public  UserSpace getUserSpaceUser(String userId){
        UserSpace space=(UserSpace)  redisConfig.getCacheObject(HttpeCode.redis_userid_space+userId);
        if (null==space){
            space=new UserSpace();
            space.setUsespace(0L);
            space.setTotalSpace(getSenderDtodefault().getUserInitUserSpace()*HttpeCode.MB);
            saveUserid_space(userId,space);
        }
        return  space;
    }
}
