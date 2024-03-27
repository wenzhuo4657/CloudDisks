package cn.wenzhuo4657.service.impl;

import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.domain.dto.*;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.enums.*;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.domain.query.SimplePage;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.mapper.UserInfoMapper;
import cn.wenzhuo4657.service.FileInfoService;
import cn.wenzhuo4657.utils.BeancopyUtils;
import cn.wenzhuo4657.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件信息(FileInfo)表服务实现类
 *
 * @author makejava
 * @since 2024-03-22 20:15:07
 */
@Service("fileInfoService")
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {


    @Resource
    private FileInfoMapper fileInfoMapper;

    /**
    * @Author wenzhuo4657
    * @Description
    * @Date 19:20 2024-03-23
    * @Param [query] 这里的query存储的数据 表示page查询的条件
    * @return cn.wenzhuo4657.domain.dto.PaginationResultDto
    **/
    @Override
    public PaginationResultDto<FileInfoDto> findListBypage(FileInfoQuery query) {
        int count=fileInfoMapper.selectCount(query);
        int pageSize=query.getPageSize()==null? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage IF=new SimplePage(query.getPageNo(), count,pageSize);
        query.setSimplePage(IF);
        List<FileInfoDto> list=BeancopyUtils.copyBeanList(fileInfoMapper.findListByInfoQuery(query), FileInfoDto.class);
        PaginationResultDto<FileInfoDto> resultDto=
                new PaginationResultDto<>(count,IF.getPageSize(),IF.getPageNo(),IF.getPageTotal(),list);
        return  resultDto;
    }

    @Resource
    private redisComponent redisComponent;

    @Resource
    private  appconfig appconfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadResultDto uploadFile(SessionDto sessionDto, String fileId, MultipartFile file, String filename, String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
        UploadResultDto resultDto=new UploadResultDto();
        if (StringUtil.isEmpty(fileId)){
            fileId=StringUtil.getRandom_Number(HttpeCode.code_length);
        }
        resultDto.setFileid(fileId);
        Date curtime=new Date();
        UserSpace userSpace= redisComponent.getUserSpaceUser(sessionDto.getUserId());

        if (chunkIndex==0){
            FileInfoQuery infoQuery=new FileInfoQuery();
            infoQuery.setFileMd5(fileMd5);
            infoQuery.setSimplePage(new SimplePage(0,1));
            infoQuery.setStatus(FileStatusEnums.USING.getStatus());
            List<FileInfo> dbFileList=fileInfoMapper.findListByInfoQuery(infoQuery);
            if (!dbFileList.isEmpty()){
                FileInfo doFile=dbFileList.get(0);
                if (doFile.getFileSize()+userSpace.getUsespace()>userSpace.getTotalSpace()){
                    throw  new SystemException(ResponseEnum.CODE_904);
                }
                doFile.setFileId(fileId);
                doFile.setFilePid(filePid);
                doFile.setUserId(sessionDto.getUserId());
                doFile.setCreateTime(curtime);
                doFile.setLastUpdateTime(curtime);
                doFile.setStatus(FileStatusEnums.USING.getStatus());
                doFile.setDelFlag(FileDefalgEnums.USING.getStatus());
                doFile.setFileMd5(fileMd5);

                filename=autoRename(filePid,sessionDto.getUserId(),filename);
                doFile.setFileName(filename);
                this.fileInfoMapper.insert(doFile);
                resultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
                updateUserSpace(sessionDto,doFile.getFileSize());
                return resultDto;
            }
                //  wenzhuo TODO 2024/3/27 : 分片上传待完成
//            临时存储目录
            String tempfolder=appconfig.getProjectFolder()+HttpeCode.Tempfolder_UploadFile;


        }
        return resultDto;
    }
    @Resource
    private UserInfoMapper userInfoMapper;

    private void updateUserSpace(SessionDto sessionDto, Long fileSize) {
        Integer count=userInfoMapper.updateUserSpace(sessionDto.getUserId(),fileSize);
        if (count==0){
            throw  new SystemException(ResponseEnum.CODE_904);
        }
        //同步到redis中
        UserSpace space=redisComponent.getUserSpaceUser(sessionDto.getUserId());
        space.setUsespace(space.getUsespace()+fileSize);
        redisComponent.saveUserid_space(sessionDto.getUserId(),space);

    }

    /**
    * @Author wenzhuo4657
    * @Description 只有在该切片文件数量大于0时，才会对其进行重命名
    * @Date 20:26 2024-03-26
    * @Param [filePid, userId, filename]
    * @return java.lang.String
    **/
    private String autoRename(String filePid, String userId, String filename) {
        FileInfoQuery fileInfoQuery=new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setDelFlag(FileDefalgEnums.USING.getStatus());
        fileInfoQuery.setFileName(filename);
        Integer count=this.fileInfoMapper.selectCount(fileInfoQuery);
        if (count>0){
            filename=StringUtil.rename(filename);
        }
        return  filename;
    }
}
