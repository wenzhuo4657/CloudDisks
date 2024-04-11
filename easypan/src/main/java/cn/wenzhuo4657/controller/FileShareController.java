package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.controller.support.ControllerSupport;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.FileInfoDto;
import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.entity.FileShare;
import cn.wenzhuo4657.domain.enums.FileDefalgEnums;
import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.domain.query.FileShareQuery;
import cn.wenzhuo4657.service.FileShareService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Arrays;

/**
 * (FileShare)表控制层
 *
 * @author makejava
 * @since 2024-04-10 18:39:01
 */
@RestController
@RequestMapping("/share")
public class FileShareController extends ControllerSupport {
    @Resource
    private FileShareService fileShareService;

    @PostMapping("/loadShareList")
    @Global_interceptor
    public ResponseVo loadDataList(HttpSession session , FileShareQuery query){
        query.setOrderBy("share_time desc");
        SessionDto sessionDto=getUserInfofromSession(session);
        query.setUserId(sessionDto.getUserId());
        PaginationResultDto<FileInfoDto> resultDto=fileShareService.findListBypage(query);
        return  ResponseVo.ok(resultDto);
    }
    @PostMapping("/shareFile")
    @Global_interceptor
    public ResponseVo shareFile(HttpSession session ,
                                  @VerifyParam(required = true) String fileId,
                                  @VerifyParam(required=true) Integer validType,
                                String code){
        SessionDto sessionDto=getUserInfofromSession(session);
        FileShare share=new FileShare();
        share.setCode(code);
        share.setFileId(fileId);
        share.setUserId(sessionDto.getUserId());
        share.setValidType(validType);
        fileShareService.saveShare(share);
        return  ResponseVo.ok();
    }

    @PostMapping("/cancelShare")
    @Global_interceptor
    public ResponseVo cancelShare(HttpSession session ,
                                @VerifyParam(required = true) String shareIds){
        SessionDto sessionDto=getUserInfofromSession(session);
        fileShareService.delFileShareBatch(sessionDto.getUserId(), shareIds.split(","));
        return  ResponseVo.ok();
    }







}

