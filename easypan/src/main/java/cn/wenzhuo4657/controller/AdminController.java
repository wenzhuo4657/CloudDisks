package cn.wenzhuo4657.controller;

import cn.wenzhuo4657.annotation.Global_interceptor;
import cn.wenzhuo4657.annotation.VerifyParam;
import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.dto.SenderDtoDefault;
import cn.wenzhuo4657.domain.dto.UserInfoVo;
import cn.wenzhuo4657.domain.query.UserInfoQuery;
import cn.wenzhuo4657.service.UserInfoService;
import cn.wenzhuo4657.utils.BeancopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @className: AdminController
 * @author: wenzhuo4657
 * @date: 2024/4/11 10:20
 * @Version: 1.0
 * @description:
 */

@RestController("adminController")
@RequestMapping("/admin")
public class AdminController {


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



}