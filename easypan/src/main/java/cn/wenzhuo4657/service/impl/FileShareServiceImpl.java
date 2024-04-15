package cn.wenzhuo4657.service.impl;

import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.dto.SessionShareDto;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.entity.FileShare;
import cn.wenzhuo4657.domain.enums.*;
import cn.wenzhuo4657.domain.query.FileShareQuery;
import cn.wenzhuo4657.domain.query.SimplePage;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.mapper.FileShareMapper;
import cn.wenzhuo4657.service.FileShareService;
import cn.wenzhuo4657.utils.DateUtil;
import cn.wenzhuo4657.utils.StringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * (FileShare)表服务实现类
 *
 * @author makejava
 * @since 2024-04-10 18:32:45
 */
@Service("fileShareService")
public class FileShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare> implements FileShareService {
    @Resource
    private FileShareMapper fileShareMapper;
     @Override
    public PaginationResultDto findListBypage(FileShareQuery query) {
        int count = fileShareMapper.selectCount(query);
        int pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage IF = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(IF);
        query.setQueryFileName(true);
         List<FileShare> list = fileShareMapper.selectListByQuery(query);
         PaginationResultDto<FileShare> resultDto =
                new PaginationResultDto<>(count, IF.getPageSize(), IF.getPageNo(), IF.getPageTotal(), list);
        return resultDto;
    }

    @Resource
    private FileInfoMapper fileInfoMapper;
    @Override
    public void saveShare(FileShare share) {
        ShareValidTypeEnums shareValidTypeEnums=ShareValidTypeEnums.getByType(share.getValidType());
        FileInfo fileInfo=fileInfoMapper.selectByFileidAndUserid(share.getFileId(),share.getUserId());
        if (Objects.isNull(shareValidTypeEnums)&&Objects.isNull(fileInfo)){
            throw new SystemException(ResponseEnum.CODE_600);
        }
        if (!shareValidTypeEnums.getType().equals(ShareValidTypeEnums.FOREVER.getType())){
            share.setExpireTime(DateUtil.getAfterDate(shareValidTypeEnums.getDays()));
        }

        Date currtime=new Date();
        share.setShareTime(currtime);
        if (StringUtil.isEmpty(share.getCode())){
            share.setCode(StringUtil.getRandom_Number(HttpeCode.code_length));
        }
        share.setShareId(StringUtil.getRandom_Number(HttpeCode.code_length_20));
        fileShareMapper.insert(share);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFileShareBatch(String userId, String[] fileIdArray) {
        int count=fileShareMapper.delBatch(userId,fileIdArray);
        if (count!=fileIdArray.length){
            throw  new SystemException(ResponseEnum.CODE_600);//一旦返回了无效的fileid，直接回滚，操作无效
        }
    }

    @Override
    public SessionShareDto checkShareCode(String shareId, String code) {
        FileShare share=this.fileShareMapper.selectByShareId(shareId);
        if (Objects.isNull(share) || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new SystemException(ResponseEnum.CODE_902);
        }
        if (!share.getCode().equals(code)){
            throw  new SystemException("提取码错误");
        }
        fileShareMapper.updateByShareId(shareId);
        SessionShareDto sessionShareDto=new SessionShareDto();
        sessionShareDto.setShareUserId(share.getUserId());
        sessionShareDto.setShareId(shareId);
        sessionShareDto.setExpireTime(share.getExpireTime());
        sessionShareDto.setFileId(share.getFileId());
        return sessionShareDto;
    }
}
