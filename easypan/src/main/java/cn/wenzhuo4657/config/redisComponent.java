package cn.wenzhuo4657.config;

import cn.wenzhuo4657.domain.HttpeCode;
import cn.wenzhuo4657.domain.dto.SenderDtoDefault;
import cn.wenzhuo4657.domain.dto.UserSpace;
import org.springframework.data.redis.connection.convert.StringToRedisClientInfoConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class redisComponent {
    @Resource
    private RedisConfig redisConfig;

    public SenderDtoDefault getSenderDtodefault(){
        SenderDtoDefault senderDtoDefault= (SenderDtoDefault) redisConfig.get(HttpeCode.redis_Mail_Key);
        if (null==senderDtoDefault){
            senderDtoDefault=new SenderDtoDefault();
            redisConfig.set(HttpeCode.redis_Mail_Key,senderDtoDefault);
        }
        return  senderDtoDefault;
    }
    public UserSpace saveUserid_space(String userID, String total_Space){
        UserSpace userSpace= (UserSpace) redisConfig.get(HttpeCode.redis_userid_space+userID);
        if (null==userSpace){
            userSpace=new UserSpace();
            userSpace.setTotalSpace(Long.parseLong(total_Space));
            redisConfig.set(HttpeCode.redis_userid_space+userID,userSpace);
        }
        return  userSpace;
    }

    public  UserSpace getUserSpaceUser(String userId){
        UserSpace space=(UserSpace)  redisConfig.get(HttpeCode.redis_userid_space+userId);
        if (null==space){
            space=new UserSpace();
            space.setUsespace(0L);
            space.setTotalSpace(getSenderDtodefault().getUserInitUserSpace()*HttpeCode.MB);
            saveUserid_space(userId, String.valueOf(space.getTotalSpace()));
        }
        return  space;
    }
}
