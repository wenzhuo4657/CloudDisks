package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.controller.support.CommonFileSupport;
import cn.wenzhuo4657.controller.support.ControllerSupport;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.FileInfoDto;
import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.dto.UploadResultDto;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.enums.FileCategoryEnums;
import cn.wenzhuo4657.domain.enums.FileDefalgEnums;
import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.service.FileInfoService;
import com.sun.deploy.net.HttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
        super.getVideoFile(response,sessionDto,fileId);
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
        super.getFile(response,sessionDto,fileId);
    }

    @PostMapping("newFoloder")
    @Global_interceptor
    public  ResponseVo newFoloder(HttpSession session,String filePid,String fileName){
        FileInfo fileInfo = fileInfoService.newFolder(fileName, filePid, getUserInfofromSession(session));
        return  ResponseVo.ok(fileInfo);
    }


}

