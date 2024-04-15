package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.controller.support.CommonFileSupport;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.*;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.entity.FileShare;
import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.domain.enums.*;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.mapper.FileShareMapper;
import cn.wenzhuo4657.mapper.UserInfoMapper;
import cn.wenzhuo4657.service.FileInfoService;
import cn.wenzhuo4657.service.FileShareService;
import cn.wenzhuo4657.utils.BeancopyUtils;
import cn.wenzhuo4657.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @className: WebShareController
 * @author: wenzhuo4657
 * @date: 2024/4/11 20:43
 * @Version: 1.0
 * @description:
 */

@RequestMapping("showShare")
@RestController("WebShareController")
public class WebShareController extends CommonFileSupport {


    @PostMapping("/getShareLoginInfo")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public ResponseVo recoverFile(HttpSession session, @VerifyParam(required = true) String shareId) {
        SessionShareDto sessionShareDto = getSessionSharefromSession(session, shareId);
        if (Objects.isNull(sessionShareDto)) {
            return ResponseVo.ok();
        }
        ShareInfoVo shareInfoVo = getShareInfoCommon(shareId);
        SessionDto userDto = getUserInfofromSession(session);
        if (!Objects.isNull(userDto) && userDto.getUserId().equals(sessionShareDto.getShareUserId())) {
            shareInfoVo.setCurrentUser(true);
        } else {
            shareInfoVo.setCurrentUser(false);
        }
        return ResponseVo.ok(getShareInfoCommon(shareId));
    }


    @RequestMapping("/getShareInfo")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public ResponseVo getShareInfo( @VerifyParam(required = true) String shareId) {
        return ResponseVo.ok(getShareInfoCommon(shareId));
    }

    @Resource
    private FileShareMapper fileShareMapper;

    @Resource
    private FileInfoMapper fileInfoMapper;
    @Resource
    private UserInfoMapper userInfoMapper;

    private ShareInfoVo getShareInfoCommon(String shareId) {
        FileShare share = fileShareMapper.selectByShareId(shareId);
        if (Objects.isNull(share) || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new SystemException(ResponseEnum.CODE_902);
        }
        ShareInfoVo shareInfoVo = BeancopyUtils.copyBean(share, ShareInfoVo.class);
        FileInfo info = fileInfoMapper.selectByFileidAndUserid(share.getFileId(), share.getUserId());
        if (Objects.isNull(info) || (!FileStatusEnums.USING.getStatus().equals(info.getStatus()))) {
            throw new SystemException(ResponseEnum.CODE_902);
        }
        shareInfoVo.setFileName(info.getFileName());
        UserInfo userInfo =userInfoMapper.selectById(share.getUserId());
        shareInfoVo.setAvatar(userInfo.getQqAvatar());
        shareInfoVo.setNickName(userInfo.getUserName());
        shareInfoVo.setUserId(userInfo.getUserId());
        return  shareInfoVo;
    }


    @Resource
    private FileShareService fileShareService;


    @RequestMapping("/checkShareCode")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public ResponseVo checkShareCode( HttpSession session,
                                      @VerifyParam(required = true) String shareId,
                                      @VerifyParam(required = true) String code) {
        SessionShareDto sessionShareDto=fileShareService.checkShareCode(shareId,code);
        session.setAttribute(HttpeCode.SessionShareDto_Key+shareId,sessionShareDto);
        return ResponseVo.ok();
    }


    @Resource
    private FileInfoService fileInfoService;

    @RequestMapping("/loadFileList")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public ResponseVo loadFileList( HttpSession session,
                                      @VerifyParam(required = true) String shareId,
                                      @VerifyParam(required = true) String filePid) {

        SessionShareDto sessionShareDto=checkShare(session,shareId);
        FileInfoQuery query=new FileInfoQuery();
        if (!StringUtil.isEmpty(filePid)&&!HttpeCode.zero_str.equals(filePid)){
            //  wenzhuo TODO 2024/4/14 : 此处没有对参数校验，因为懒-------
            query.setFilePid(filePid);
        }else {
            query.setFileId(sessionShareDto.getFileId());
        }
        query.setUserId(getUserInfofromSession(session).getUserId());
        query.setOrderBy(HttpeCode.LoadDataList_File_sort);
        query.setDelFlag(FileDefalgEnums.USING.getStatus());
        PaginationResultDto<FileInfoDto> resultDto=fileInfoService.findListBypage(query);
        return  ResponseVo.ok(resultDto);
    }

    private SessionShareDto checkShare(HttpSession session, String shareId) {
        SessionShareDto sessionShareDto=getSessionSharefromSession(session,shareId);
        if (Objects.isNull(sessionShareDto)) {
            throw  new SystemException(ResponseEnum.CODE_903);
        }

        if (sessionShareDto.getExpireTime()!=null&&new Date().after(sessionShareDto.getExpireTime())){
            throw new SystemException(ResponseEnum.CODE_902);
        }
        return  sessionShareDto;

    }

    @RequestMapping("/getFolderInfo")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public  ResponseVo getFolderInfo(HttpSession session,
                                     @VerifyParam(required = true) String path,
                                     @VerifyParam(required = true) String shareId){
        SessionShareDto sessionShareDto=checkShare(session,shareId);
        return  super.getFolderInfo(sessionShareDto.getShareUserId(),path);
    }

    @RequestMapping("getFile/{shareId}/{fileId}")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public  void  getFile(HttpSession session, HttpServletResponse response,
                          @PathVariable("fileId") String fileId,@PathVariable("shareId") String shareId){
        SessionShareDto sessionShareDto=checkShare(session,shareId);
        super.getFile(response,sessionShareDto.getShareUserId(),fileId);
    }

    @RequestMapping("ts/getVideoInfo/{shareId}/{fileId}")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public  void  getVideoInfo(HttpSession session, HttpServletResponse response,
                               @PathVariable("shareId") String shareId, @PathVariable("fileId") String fileId){
        SessionShareDto sessionShareDto=checkShare(session,shareId);
        super.getVideoFile(response,sessionShareDto.getShareUserId(),fileId);
    }

    @PostMapping("createDownloadUrl/{shareId}/{fileId}")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public  ResponseVo createDownloadUrl(
            HttpSession session,
            @PathVariable(value = "fileId") String fileId,
            @PathVariable("shareId") String shareId){
        SessionShareDto sessionShareDto=checkShare(session,shareId);
        return  super.createDownloadUrl(fileId,sessionShareDto.getShareUserId());

    }

    @Resource
    private  appconfig appconfig;
    @Resource
    private redisComponent redisComponent;
    @GetMapping("download/{code}")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public  void  download(HttpServletRequest request,
                           HttpServletResponse response,
                           @PathVariable(value = "code") String code ) throws UnsupportedEncodingException {
        super.download(request,response,code);
    }

    @PostMapping("/saveShare")
    @Global_interceptor(checkLogin = false, checkparams = true)
    public ResponseVo saveShare(HttpSession session,
                                @VerifyParam(required = true) String shareId,
                                @VerifyParam(required = true) String shareFileIds,
                                @VerifyParam(required = true) String myFolderId){
        //  wenzhuo TODO 2024/4/14 :  保存网盘接口待做
//        SessionShareDto shareSessionDto = checkShare(session, shareId);
//        SessionDto webUserDto =getUserInfofromSession(session);
//        if (shareSessionDto.getShareUserId().equals(webUserDto.getUserId())) {
//            throw new SystemException(HttpeCode.SHARE_SAVE_ERROR);
//        }
//        fileInfoService.saveShare(shareSessionDto.getFileId(), shareFileIds, myFolderId, shareSessionDto.getShareUserId(), webUserDto.getUserId());
        return ResponseVo.ok();
    }





}