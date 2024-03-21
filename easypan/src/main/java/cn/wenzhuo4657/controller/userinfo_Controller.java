package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.domain.HttpeCode;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.VerifyRegexEnum;
import cn.wenzhuo4657.domain.appconfig;
import cn.wenzhuo4657.domain.dto.CreateImageCode;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.dto.UserSpace;
import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.service.UserInfoService;
import cn.wenzhuo4657.service.impl.EmailCodeServiceImpl;
import cn.wenzhuo4657.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


@RestController("userinfoController")//这里指定了rest控制器的名称，默认情况下是类名
public class userinfo_Controller extends  ControllerSupport{
    @Resource
    private UserInfoService userInfoService;


    @RequestMapping("checkCode")
    public void checkode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        CreateImageCode vcode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vcode.getCode();

        if (type == null || type == 0) {
            session.setAttribute(HttpeCode.Check_NO_Ok, code);
        } else {
            session.setAttribute(HttpeCode.Check_Ok, code);
        }
        vcode.write(response.getOutputStream());
    }

    @Autowired
    EmailCodeServiceImpl emailCodeService;


    @PostMapping("sendEmailCode")
    @Global_interceptor(checkparams = true)
    public ResponseVo sendEmail(HttpSession session, @VerifyParam(required = true, regex = VerifyRegexEnum.EMALL) String email, String checkCode, Integer type) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(HttpeCode.Check_Ok))) {
                throw new SystemException(HttpeCode.Image_no_OK);
            }
            emailCodeService.sendEmailcode(email, type);
            return ResponseVo.ok();
        } finally {
        session.removeAttribute(HttpeCode.Check_Ok);
//        无论是否成功发送，图形验证码都将再次刷新，需要重新进行图形验证
        }
    }

    @PostMapping("/register")
    @Global_interceptor(checkparams = true)
    public ResponseVo register(HttpSession session,  String email,String nickName,  String password, String checkCode, String emailCode) {
        try {
            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(HttpeCode.Check_Ok))) {
                throw new SystemException(HttpeCode.Image_no_OK);
            }
            userInfoService.register(email, nickName, password, emailCode);
            return ResponseVo.ok();
        } finally {
        session.removeAttribute(HttpeCode.Check_Ok);
//        无论是否成功发送，图形验证码都将再次刷新，需要重新进行图形验证
        }
    }

    @PostMapping("/login")
    @Global_interceptor(checkparams = true)
    public ResponseVo login(HttpSession session, String email, String password, String checkCode, Integer type) {
        try {
//            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(HttpeCode.Check_NO_Ok))) {
//                throw new SystemException(HttpeCode.Image_no_OK);
//            }
            SessionDto sessionDto = userInfoService.login(email,  password);
            session.setAttribute(HttpeCode.SessionDto_key,sessionDto);
            return ResponseVo.ok(sessionDto.toString());
        } finally {
        session.removeAttribute(HttpeCode.Check_NO_Ok);
//        无论是否成功发送，图形验证码都将再次刷新，需要重新进行图形验证
        }
    }

    @PostMapping("/resetPwd")
    @Global_interceptor(checkparams = true)
    public ResponseVo resetPwd(HttpSession session,
                               String email,
                               String password, String checkCode,
                               String emailCode, Integer type) {
        try {
//            if (!checkCode.equalsIgnoreCase((String) session.getAttribute(HttpeCode.Check_NO_Ok))) {
//                throw new SystemException(HttpeCode.Image_no_OK);
//            }
            userInfoService.resetPwd(email,  password, emailCode);
            return ResponseVo.ok();
        } finally {
            session.removeAttribute(HttpeCode.Check_NO_Ok);
//        无论是否成功发送，图形验证码都将再次刷新，需要重新进行图形验证
        }
    }

    @Resource
    private appconfig  appconfig;

    private Logger logger= LoggerFactory.getLogger(userinfo_Controller.class);


    @GetMapping("/getAvatar/{userId}")
    @Global_interceptor(checkparams = true)
    public  void getAvatar(HttpServletResponse response,
                                 @PathVariable ("userId") String userid){

        String avatarFolder= appconfig.getProjectFolder()+HttpeCode.File_Folder_root+HttpeCode.File_Folder_AvvatarName;
        File folder=new File(avatarFolder);
        if(!folder.exists()){
            folder.mkdirs();
        }
        String userid_Avatar=avatarFolder+"/"+userid+".jpg";
        File avatar=new File(userid_Avatar);
        if (!avatar.exists()){
            String default_avatar=avatarFolder+"/default_avatar.jpg";
            File avater_default=new File(default_avatar);
            if (!avater_default.exists()){
                printNoDefaultImage(response);
            }
            userid_Avatar=default_avatar;

        }
        response.setContentType("image/jpg");
        readFile(response,userid_Avatar);
    }

    private  static  final  String CONTENT_TYPE="Content-Type";
    private  static final String CONTENE_TYPE_VALUE="application/json;charset=UTF-8";


    public  void printNoDefaultImage(HttpServletResponse response){
        response.setHeader(CONTENT_TYPE,CONTENE_TYPE_VALUE);
        response.setStatus(HttpStatus.OK.value());
        PrintWriter writer=null;
        try {
            writer=response.getWriter();
            writer.print("请在存放默认头像：default_avatar.jpg");
            writer.close();
        }catch (Exception e){
            logger.info("输出无默认图失败，{}",e);
        }finally {
            writer.close();
        }

    }

    @RequestMapping("/getUserInfo")
    @Global_interceptor(checkparams = true)
    public ResponseVo getUserInfo(HttpSession session){
        SessionDto sessionDto=getUserInfofromSession(session);
        return ResponseVo.ok();
    }

    @Resource
    private redisComponent redisComponent;

    @PostMapping ("/getUseSpace")
    @Global_interceptor(checkparams = true)
    public ResponseVo getUseSpace(HttpSession session){
        UserSpace userSpace=redisComponent.getUserSpaceUser(getUserInfofromSession(session).getUserId());
        return ResponseVo.ok(userSpace.toString());
    }

    @GetMapping("/logout")
    @Global_interceptor(checkparams = true)
    public  ResponseVo logout(HttpSession session){
        session.invalidate();
        return  ResponseVo.ok();
    }

    @PostMapping("/updateUserAvatar")
    @Global_interceptor
    public  ResponseVo updateUserAvatar(HttpSession session, MultipartFile avatar){
        SessionDto useINFO=getUserInfofromSession(session);
        String basefolder=appconfig.getProjectFolder()+HttpeCode.File_Folder_root;
        File rootfolder_Avatar=new File(basefolder+HttpeCode.File_Folder_AvvatarName);
        if (!rootfolder_Avatar.exists()){
            rootfolder_Avatar.exists();
        }
        File targetFile=new File(rootfolder_Avatar.getPath()+"/"+useINFO.getUserId()+".jpg");
        try {
            avatar.transferTo(targetFile);//将文件写入指定路径中
        } catch (IOException e) {
            logger.info("上传头像失败，{}",e);
        }
        UserInfo userInfo=new UserInfo();
        userInfo.setQqAvatar("");//将qq头像置空
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserId,useINFO.getUserId());
        userInfoService.update(userInfo,wrapper);
        useINFO.setAvatar(null);
        session.setAttribute(HttpeCode.SessionDto_key,useINFO);
        return ResponseVo.ok();
    }

    @PostMapping("/updatePassword")
    @Global_interceptor(checkparams = true)
    public  ResponseVo updatePassword(HttpSession session
            ,@VerifyParam(required = true,regex = VerifyRegexEnum.PASSWORD,min = 5,max = 20)String password){
        SessionDto sessionDto=getUserInfofromSession(session);
        UserInfo userInfo=new UserInfo();
        userInfo.setPassword(StringUtil.getMd5(password));
        LambdaQueryWrapper<UserInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserId,sessionDto.getUserId());
        userInfoService.update(userInfo,wrapper);
        return  ResponseVo.ok();
    }






}
