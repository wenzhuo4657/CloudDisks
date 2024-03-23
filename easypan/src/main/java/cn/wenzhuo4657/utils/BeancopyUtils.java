package cn.wenzhuo4657.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class BeancopyUtils {

    private BeancopyUtils(){

    }

    public static  <V> V copyBean(Object source,Class<V> vClass){
        V result=null;
        try {
            result=vClass.newInstance();
            BeanUtils.copyProperties(source,result);
        }catch ( Exception e){
            System.out.println(e);
            throw  new RuntimeException(e);
        }
        return  result;
    }

    public static  <O,V>  List<V>  copyBeanList(List<O> sources,Class<V> tClass){
        return sources.stream()
                .map(o -> copyBean(o, tClass))
                .collect(Collectors.toList());
    }

}
