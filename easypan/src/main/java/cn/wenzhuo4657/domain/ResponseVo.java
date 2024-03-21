package cn.wenzhuo4657.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseVo<T> {
    private  String status;
    private  Integer code;
    private  String info;
    private T data;


    public static ResponseVo ok() {
        ResponseVo r=new ResponseVo<>();
        r.setCode(200);
        r.setInfo("成功！！！");
        return  r;
    }
    public static ResponseVo ok(String info) {
        ResponseVo r=ok();
        r.setData(info.toString());
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
