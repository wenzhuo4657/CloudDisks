package cn.wenzhuo4657.domain;

import cn.wenzhuo4657.domain.dto.UserSpace;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseVo<T> implements Serializable {
    private  String status;
    private  Integer code;
    private  String info;
    private T data;


    public static ResponseVo ok() {
        ResponseVo r=new ResponseVo<>();
        r.setStatus("success");
        r.setCode(200);
        r.setInfo("成功！！！");
        return  r;
    }
    public static ResponseVo ok(Object info) {
        ResponseVo r=ok();
        r.setData(info);
        return  r;
    }



    public static ResponseVo error(String info){
        ResponseVo r=new ResponseVo<>();
        r.setCode(500);
        r.setInfo(info);
        return  r;
    }

    public static ResponseVo error(String status,int code,String info,Object data){
        ResponseVo r=new ResponseVo<>();
        r.setStatus(status);
        r.setCode(500);
        r.setInfo(info);
        r.setData(data);
        return  r;
    }
}
