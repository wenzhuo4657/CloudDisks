package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.FileInfoDto;
import cn.wenzhuo4657.domain.dto.PaginationResultDto;
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


}

