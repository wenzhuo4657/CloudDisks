package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
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
import cn.wenzhuo4657.utils.BeancopyUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Optional;

/**
 * 文件信息(FileInfo)表控制层
 *
 * @author makejava
 * @since 2024-03-22 20:18:29
 */
@RestController
@RequestMapping("/file")
public class FileInfoController extends  ControllerSupport{

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
}

