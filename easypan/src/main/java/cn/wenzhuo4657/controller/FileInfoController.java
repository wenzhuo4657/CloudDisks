package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.controller.support.CommonFileSupport;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.*;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.enums.*;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.service.FileInfoService;
import cn.wenzhuo4657.utils.BeancopyUtils;
import cn.wenzhuo4657.utils.StringUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.Optional;

/**
 * 文件信息(FileInfo)表控制层
 *
 * @author makejava
 * @since 2024-03-22 20:18:29
 */
@RestController
@RequestMapping("/file")
public class FileInfoController extends CommonFileSupport {

    @Resource
    private FileInfoService fileInfoService;

    @PostMapping("/loadDataList")
    @Global_interceptor
    public ResponseVo loadDataList(HttpSession session , FileInfoQuery query,String category){
//        根据前段请求参数动态变化
        Optional<FileCategoryEnums> fileCategoryEnums=Optional.ofNullable(FileCategoryEnums.getByCode(category));
        fileCategoryEnums.ifPresent(var ->query.setFileCategory(var.getCategory()));

//        默认
        query.setUserId(getUserInfofromSession(session).getUserId());
        query.setOrderBy(HttpeCode.LoadDataList_File_sort);
        query.setDelFlag(FileDefalgEnums.USING.getStatus());
        PaginationResultDto<FileInfoDto> resultDto=fileInfoService.findListBypage(query);
        return  ResponseVo.ok(resultDto);
    }

    @PostMapping(value = "/uploadFile")
    @Global_interceptor
    public  ResponseVo uploadFile(
            HttpSession session,
            String fileId, MultipartFile file,
            @VerifyParam(required = true) String fileName,
            @VerifyParam(required = true) String filePid,
            @VerifyParam(required = true) String fileMd5,
            @VerifyParam(required = true) Integer chunkIndex,
            @VerifyParam(required = true) Integer chunks
            ){
        SessionDto sessionDto=getUserInfofromSession(session);
        UploadResultDto resultDto=fileInfoService.uploadFile(sessionDto,fileId,file,fileName,filePid,fileMd5,chunkIndex,chunks);

        return  ResponseVo.ok(resultDto);
    }

    @GetMapping(value = "/getImage/{imageFolder}/{imageName}")
    public  void  getImage(HttpServletResponse response,
                           @PathVariable(value = "imageFolder")String imageFloder,
                           @PathVariable(value = "imageName") String imageName){
        super.getImage(response,imageFloder,imageName);
    }

/**
* @Author wenzhuo4657
* @Description  切片文件预览
 * 视频预览：第一次接受的fileId为真实的数据库索引fileId，要求返回视频文件的index.m3u8索引文件，
 * 其后前端根据索引文件依次发起请求得到视频切片，fileId为切片文件名称
* @Date 19:03 2024-03-31
* @Param [session, response, fileId]
* @return void
**/
    @RequestMapping("ts/getVideoInfo/{fileId}")
    @Global_interceptor
    public  void  getVideoInfo(HttpSession session, HttpServletResponse response,@PathVariable("fileId") String fileId){
        SessionDto sessionDto=getUserInfofromSession(session);
        super.getVideoFile(response,sessionDto.getUserId(),fileId);
    }


    /**
    * @Author wenzhuo4657
    * @Description 预览文件为非切片文件，
    * @Date 19:37 2024-03-31
    * @Param [session, response, fileId]
    * @return void
    **/
    @RequestMapping("getFile/{fileId}")
    @Global_interceptor
    public  void  getFile(HttpSession session, HttpServletResponse response,@PathVariable("fileId") String fileId){
        SessionDto sessionDto=getUserInfofromSession(session);
        super.getFile(response,sessionDto.getUserId(),fileId);
    }

    @PostMapping("newFoloder")
    @Global_interceptor
    public  ResponseVo newFoloder(HttpSession session,String filePid,String fileName){
        FileInfo fileInfo = fileInfoService.newFolder(fileName, filePid, getUserInfofromSession(session));
        return  ResponseVo.ok(fileInfo);
    }

/**
* @Author wenzhuo4657
* @Description 返回目录信息，并按照上下级顺序
* @Date 12:37 2024-04-02
* @Param [session, path]
* @return cn.wenzhuo4657.domain.ResponseVo
**/
    @PostMapping("getFolderInfo")
    @Global_interceptor
    public  ResponseVo getFolderInfo(HttpSession session,@VerifyParam(required = true) String path){
        SessionDto sessionDto=getUserInfofromSession(session);
        return  super.getFolderInfo(sessionDto.getUserId(),path);
    }

    @PostMapping("rename")
    @Global_interceptor
    public  ResponseVo rename(HttpSession session,
                              @VerifyParam(required = true)String fileId,
                              @VerifyParam(required = true)String fileName){
        SessionDto sessionDto=getUserInfofromSession(session);
        FileInfo fileInfo=fileInfoService.rename(fileId,fileName,sessionDto.getUserId());
        return  ResponseVo.ok(BeancopyUtils.copyBean(fileInfo,FileInfoDto.class));
    }

/**
* @Author wenzhuo4657
* @Description 具体文件跳转逻辑有前端实现，这里仅实现对信息的传输
* @Date 19:45 2024-04-02
* @Param [session,
 * filePid,父级id
 * currentFileIds 查询id不能够返回这个id,]
* @return cn.wenzhuo4657.domain.ResponseVo
**/
    @PostMapping("loadAllFolder")
    @Global_interceptor
    public  ResponseVo loadAllFolder(HttpSession session,
                              @VerifyParam(required = true)String filePid,
                              String currentFileIds){
        SessionDto sessionDto=getUserInfofromSession(session);
        FileInfoQuery infoQuery=new FileInfoQuery();
        infoQuery.setUserId(sessionDto.getUserId());
        infoQuery.setFilePid(filePid);
        infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        if (!StringUtil.isEmpty(currentFileIds)){
            infoQuery.setExcludeFileIdArray(currentFileIds.split(","));
        }
        infoQuery.setDelFlag(FileDefalgEnums.USING.getStatus());
        infoQuery.setOrderBy("create_time desc");
        PaginationResultDto<FileInfoDto> infoList=fileInfoService.findListBypage(infoQuery);
        return  ResponseVo.ok(BeancopyUtils.copyBeanList(infoList.getList(), FileInfoDto.class));
    }


    /**
    * @Author wenzhuo4657
    * @Description
    * @Date 20:13 2024-04-02
    * @Param [session
     * , fileIds:將要移动位置的文件
     * , filePid：目标文件件的id
     * ]
    * @return cn.wenzhuo4657.domain.ResponseVo
    **/
    @PostMapping("changeFileFolder")
    @Global_interceptor
    public  ResponseVo changeFileFolder(HttpSession session,
                              @VerifyParam(required = true)String fileIds,
                              @VerifyParam(required = true)String filePid){
        SessionDto sessionDto=getUserInfofromSession(session);
        fileInfoService.changeFileFolder(fileIds,filePid,sessionDto.getUserId());
        return  ResponseVo.ok();
    }



    @PostMapping("createDownloadUrl/{fileId}")
    @Global_interceptor(checkLogin = false)
    public  ResponseVo createDownloadUrl(HttpSession session,@PathVariable(value = "fileId") String fileId){
        SessionDto sessionDto=getUserInfofromSession(session);
        return  super.createDownloadUrl(fileId,sessionDto.getUserId());

    }



    @Resource
    private redisComponent redisComponent;
    @Resource
    private  appconfig appconfig;

//    todo 这里要实现断点续传
//    ps：这里需要场景修改前端，或者使用模板引擎做一个nginx代理的静态页面，方便后期作为个人云盘
    @GetMapping("download/{code}")
    @Global_interceptor(checkLogin = false)
    public  void  download(HttpServletRequest request,HttpServletResponse response,@PathVariable(value = "code") String code ) throws UnsupportedEncodingException {
       super.download(request,response,code);
    }


    @PostMapping("delFile")
    @Global_interceptor
    public  ResponseVo delFile(HttpSession session,String fileIds){
        SessionDto sessionDto=getUserInfofromSession(session);
        fileInfoService.removeFile(sessionDto.getUserId(),fileIds);
        return  ResponseVo.ok();
    }




}

