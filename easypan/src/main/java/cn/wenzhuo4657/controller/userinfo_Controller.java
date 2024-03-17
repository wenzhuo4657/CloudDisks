package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.domain.HttpeCode;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.VerifyRegexEnum;
import cn.wenzhuo4657.domain.dto.CreateImageCode;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.service.EmailCodeService;
import cn.wenzhuo4657.service.UserInfoService;
import cn.wenzhuo4657.service.impl.EmailCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@RestController("userinfoController")//这里指定了rest控制器的名称，默认情况下是类名
public class userinfo_Controller {
    @Resource
    private UserInfoService userInfoService;


    @RequestMapping("/checkCode")
    public  void checkode(HttpServletResponse response, HttpSession session,Integer  type) throws IOException {
        CreateImageCode vcode=new CreateImageCode(130,38,5,10);
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/jpeg");
        String code=vcode.getCode();

        if(type==null||type==0){
            session.setAttribute(HttpeCode.Check_NO_Ok,code);
        }else {
            session.setAttribute(HttpeCode.Check_Ok,code);
        }
        vcode.write(response.getOutputStream());
    }

    @Autowired
    EmailCodeServiceImpl emailCodeService;



    @PostMapping("sendEmailCode")
    @Global_interceptor(checkparams = true)
    public ResponseVo sendEmail(HttpSession session,
                                @VerifyParam(required = true,regex = VerifyRegexEnum.EMALL) String email, String checkCode, Integer type){
    try {
        if (! checkCode.equalsIgnoreCase((String) session.getAttribute(HttpeCode.Check_Ok))){
            throw new SystemException(HttpeCode.Image_no_OK);
        }
        emailCodeService.sendEmailcode(email,type);
        return ResponseVo.ok();
    }finally {
//        session.removeAttribute(HttpeCode.Check_Ok);
//        无论是否成功发送，图形验证码都将再次刷新，需要重新进行图形验证
    }
    }

}
