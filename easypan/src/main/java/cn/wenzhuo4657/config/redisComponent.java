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
            space.setUsespace(1L);
            space.setTotalSpace(getSenderDtodefault().getUserInitUserSpace()*HttpeCode.MB);
            saveUserid_space(userId,space);
        }
        return  space;
    }


    /**
    * @Author wenzhuo4657
    * @Description 对临时文件大小进行更新
    * @Date 19:42 2024-03-28
    * @Param [userId, fileId, fileSize]
    * @return void
    **/

    public  void UploadTempFileSize(String userId, String fileId, Long fileSize){
        Long cur=getNumber_Redis(HttpeCode.redis_TempSize+userId+fileId);
        if (cur!=-1L){
            redisConfig.setCacheObject(HttpeCode.redis_TempSize+userId+fileId,fileSize+cur);
        }
    }


/**
* @Author wenzhuo4657
* @Description  获取指定用户已经上传文件的分片总大小，fileId用于指定某个确定文件
* @Date 10:39 2024-03-28
* @Param [userId, fileId]
* @return java.lang.Long
**/
    public Long getTempFileSzie(String userId, String fileId) {
        Long curSzie=getNumber_Redis(HttpeCode.redis_TempSize+userId+fileId);
        return  curSzie;
    }

    /**
    * @Author wenzhuo4657
    * @Description
     * 根据key值从redis中查找值为数字（Integer或者long）,
     * 如果存在，将其强转为Long类型返回，
     * 如果没有，则返回0L，如果存在对应key,但没有符合的数据类型，则返回-1L
    * @Date 10:48 2024-03-28
    * @Param [key]
    * @return java.lang.Long
    **/
    private Long getNumber_Redis(String key) {
        Object size=redisConfig.getCacheObject(key);
        if (size==null){
            return  0L;
        }
        if (size instanceof  Integer){
            return  ((Integer) size).longValue();
        }else if (size instanceof  Long){
            return  (Long) size;
        }
        return  -1L;
    }

}
