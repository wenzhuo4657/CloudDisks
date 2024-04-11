package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.controller.support.CommonFileSupport;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.*;
import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.enums.ResponseEnum;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.domain.query.UserInfoQuery;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.service.FileInfoService;
import cn.wenzhuo4657.service.UserInfoService;
import cn.wenzhuo4657.utils.BeancopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import cn.wenzhuo4657.domain.enums.appconfig;

/**
 * @className: AdminController
 * @author: wenzhuo4657
 * @date: 2024/4/11 10:20
 * @Version: 1.0
 * @description:
 */

@RestController("adminController")
@RequestMapping("/admin")
public class AdminController extends CommonFileSupport {


    @Resource
    private redisComponent redisComponentl;

    @RequestMapping("/getSysSettings")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public ResponseVo getSysSettings(){
        return  ResponseVo.ok(redisComponentl.getSenderDtodefault());
    }

    @RequestMapping("/saveSysSettings")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public ResponseVo saveSysSettings(@VerifyParam(required = true) String registerEmailTitle,
                                      @VerifyParam(required = true) String registerEmailContent,
                                      @VerifyParam(required = true) String userInitUseSpace){
        SenderDtoDefault senderDtoDefault=new SenderDtoDefault(registerEmailTitle,registerEmailContent,userInitUseSpace);
        redisComponentl.saveSenderDtodefault(senderDtoDefault);
        return  ResponseVo.ok();
    }

    @Resource
    private UserInfoService userInfoService;
    @RequestMapping("/loadUserList")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public ResponseVo loadUserList(UserInfoQuery query){
        query.setOrderBy("creat_time desc");
        PaginationResultDto resultDto=userInfoService.findListByPage(query);
        return  ResponseVo.ok(resultDto);
    }
/**
* @Author wenzhuo4657
* @Description 如果要禁用用户，则将用户空间初始化，启用时不恢复，但这并没有将硬盘上的文件删除，
* @Date 18:34 2024-04-11
* @Param [userId, status]
* @return cn.wenzhuo4657.domain.ResponseVo
**/
    @RequestMapping("/updateUserStatus")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public ResponseVo updateUserStatus(@VerifyParam(required = true) String userId,
                                       @VerifyParam(required = true) Integer status){
        userInfoService.updateStatusByID(userId,status);
        return  ResponseVo.ok();
    }

    @RequestMapping("/updateUserSpace")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public ResponseVo updateUserSpace(@VerifyParam(required = true) String userId,
                                       @VerifyParam(required = true) Integer changeSpace){
        userInfoService.changeSpace(userId,changeSpace);
        return  ResponseVo.ok();
    }
    @Resource
    private FileInfoService fileInfoService;
    @RequestMapping("/loadFileList")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public ResponseVo loadFileList(FileInfoQuery query){
        query.setOrderBy("last_update_time desc");
        query.setQueryNickName(true);
        PaginationResultDto resultDto=fileInfoService.findListBypage(query);
        return  ResponseVo.ok(resultDto);
    }


    @RequestMapping("/getFolderInfo")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public ResponseVo getFolderInfo(String path){
        return  super.getFolderInfo(null,path);
    }

    @RequestMapping("getFile/{userId}/{fileId}")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public  void  getFile(HttpSession session, HttpServletResponse response,
                          @PathVariable("fileId") String fileId,@PathVariable("userId") String userId){
        super.getFile(response,userId,fileId);
    }

    @RequestMapping("ts/getVideoInfo/{userId}/{fileId}")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public  void  getVideoInfo(HttpSession session, HttpServletResponse response,
                               @PathVariable("userId") String userId, @PathVariable("fileId") String fileId){

        super.getVideoFile(response,userId,fileId);
    }



    @PostMapping("createDownloadUrl/{userId}/{fileId}")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public  ResponseVo createDownloadUrl(
                                         @PathVariable(value = "fileId") String fileId,
                                         @PathVariable("userId") String userId){
        return  super.createDownloadUrl(fileId,userId);

    }

    @Resource
    private redisComponent redisComponent;
    @Resource
    private appconfig appconfig;

    @GetMapping("download/{code}")
    @Global_interceptor(checkLogin = false)
    public  void  download(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "code") String code ) throws UnsupportedEncodingException {
        DownloadFileDto dto=redisComponent.getDownloadCode(code);
        if (Objects.isNull(dto)){
            throw  new SystemException(ResponseEnum.CODE_600);
        }
        String filePath= appconfig.getProjectFolder()
                + "/"+ HttpeCode.File_userid
                +"/"+dto.getFilePath();
        String fileName=dto.getFileName();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        readFile(response, filePath);
    }


    @PostMapping("delFile")
    @Global_interceptor(checkparams = true,checkAdmin = true)
    public  ResponseVo delFile(String fileIdAndUserIds){
        String[] fileIdAndUserIdArray=fileIdAndUserIds.split(",");
        for (String fileIdAndUserId:fileIdAndUserIdArray ){
            String[] fileIdAndUserIdA=fileIdAndUserId.split("_");
            fileInfoService.delFileBatch(fileIdAndUserIdA[0],fileIdAndUserIdA[1],true);
        }

        return  ResponseVo.ok();
    }






}