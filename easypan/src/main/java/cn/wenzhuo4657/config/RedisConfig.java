package cn.wenzhuo4657.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisConfig<V> {
    @Resource
    private RedisTemplate<String,V> redisTemplate;

    private  static final Logger logger= LoggerFactory.getLogger(RedisConfig.class);
    public  V get(String Key){
        return  Key== null? null: redisTemplate.opsForValue().get(Key);
    }

    public  boolean set(String key,V value){
        try {
            redisTemplate.opsForValue().set(key,value);
            return  true;
        }catch (Exception e){
            logger.error("设置rediskeykey：{}，value:{}失败",key,value);
            return  false;
        }

    }

    public  boolean setkey_time(String key,V value,long time){
        try{
            if (time>0){
                redisTemplate.opsForValue().set(key,value, time);
            }else{
                set(key,value);
                logger.info("rediskey有效时间设置失败，由于time<=0");
            }
            return  true;
        }catch ( Exception e){
            logger.error("设置rediskeykey：{}，value:{}失败",key,value);
            return  false;
        }

    }




}