package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.controller.support.ControllerSupport;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.FileInfoDto;
import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.enums.FileCategoryEnums;
import cn.wenzhuo4657.domain.enums.FileDefalgEnums;
import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.service.FileInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * @className: RecycleController
 * @author: wenzhuo4657
 * @date: 2024/4/8 19:38
 * @Version: 1.0
 * @description:  回收站功能
 */
@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ControllerSupport {
    @Resource
    private FileInfoService fileInfoService;

    @PostMapping("/loadRecycleList")
    @Global_interceptor
    public ResponseVo loadDataList(HttpSession session , Integer pageNo, Integer pageSize){
        FileInfoQuery query=new FileInfoQuery();

//        默认
        query.setUserId(getUserInfofromSession(session).getUserId());
        query.setOrderBy(HttpeCode.LoadRecycleList_File_sort);
        query.setDelFlag(FileDefalgEnums.RECYCLE.getStatus());
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        PaginationResultDto<FileInfoDto> resultDto=fileInfoService.findListBypage(query);
        return  ResponseVo.ok(resultDto);
    }



    @RequestMapping("/recoverFile")
    @Global_interceptor
    public ResponseVo recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds){
        SessionDto dto=getUserInfofromSession(session);
        fileInfoService.recoverFileBatch(dto.getUserId(),fileIds);
        return  ResponseVo.ok();
    }

    /**
    * @Author wenzhuo4657
    * @Description  此处并没有删除真实的文件，仅仅是将文件从数据库中删除
    * @Date 14:37 2024-04-10
    * @Param [session, fileIds]
    * @return cn.wenzhuo4657.domain.ResponseVo
    **/
    @RequestMapping("/delFile")
    @Global_interceptor
    public ResponseVo delFile(HttpSession session, @VerifyParam(required = true) String fileIds){
        SessionDto dto=getUserInfofromSession(session);
        fileInfoService.delFileBatch(dto.getUserId(),fileIds,false);
        return  ResponseVo.ok();
    }

}