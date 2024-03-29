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
import cn.wenzhuo4657.utils.DateUtil;
import cn.wenzhuo4657.utils.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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

    private Logger logger= LoggerFactory.getLogger(FileInfoService.class);

    /**
     * @return cn.wenzhuo4657.domain.dto.PaginationResultDto
     * @Author wenzhuo4657
     * @Description
     * @Date 19:20 2024-03-23
     * @Param [query] 这里的query存储的数据 表示page查询的条件
     **/
    @Override
    public PaginationResultDto<FileInfoDto> findListBypage(FileInfoQuery query) {
        int count = fileInfoMapper.selectCount(query);
        int pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage IF = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(IF);
        List<FileInfoDto> list = BeancopyUtils.copyBeanList(fileInfoMapper.findListByInfoQuery(query), FileInfoDto.class);
        PaginationResultDto<FileInfoDto> resultDto =
                new PaginationResultDto<>(count, IF.getPageSize(), IF.getPageNo(), IF.getPageTotal(), list);
        return resultDto;
    }

    @Resource
    private redisComponent redisComponent;

    @Resource
    private appconfig appconfig;

    /**
     * @return cn.wenzhuo4657.domain.dto.UploadResultDto
     * @Author wenzhuo4657
     * @Description 分片上传文件后合并，且需要注意的是，只会在文件分片全部上传完成后才会对redis中usespace进行更新
     * @Date 11:06 2024-03-28
     * @Param [sessionDto 存储用户信息
     * , fileId 用于标识本次文件上传，存于响应体中，并非标识分片 ，可后端自动生成
     * , file  便于前后端上传下载文件
     * , filename 文件名字
     * , filePid 文件父级
     * , fileMd5  唯一标识文件内容，用于秒传判断数据库中是否具有相同内容的文件
     * , chunkIndex  当前分片序号
     * , chunks] 上传分片总数量
     **/

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadResultDto uploadFile(SessionDto sessionDto, String fileId, MultipartFile file, String filename, String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
        UploadResultDto resultDto = new UploadResultDto();

        Boolean uploadStatas = true;
        File tempFile = null;
        try {
            if (StringUtil.isEmpty(fileId)) {

                fileId = "2";
            }
            resultDto.setFileid(fileId);
            Date curtime = new Date();
            UserSpace userSpace = redisComponent.getUserSpaceUser(sessionDto.getUserId());

            /**
             * 1，秒传判断：
             * 当传入第一个切片文件时，我们通过md5判断数据库中是否存在相同的文件，如果存在相同的文件，则无需继续上传其他切片
             * */
            if (chunkIndex == 0) {
                FileInfoQuery infoQuery = new FileInfoQuery();
                infoQuery.setFileMd5(fileMd5);
                infoQuery.setSimplePage(new SimplePage(0, 1));
                infoQuery.setStatus(FileStatusEnums.USING.getStatus());

                List<FileInfo> dbFileList = fileInfoMapper.findListByInfoQuery(infoQuery);

                if (!dbFileList.isEmpty()) {

                    FileInfo doFile = dbFileList.get(0);
                    if (doFile.getFileSize() + userSpace.getUsespace() > userSpace.getTotalSpace()) {
                        throw new SystemException(ResponseEnum.CODE_904);
                    }
                    doFile.setFileId(fileId);
                    doFile.setFilePid(filePid);
                    doFile.setUserId(sessionDto.getUserId());
                    doFile.setCreateTime(curtime);
                    doFile.setLastUpdateTime(curtime);
                    doFile.setStatus(FileStatusEnums.USING.getStatus());
                    doFile.setDelFlag(FileDefalgEnums.USING.getStatus());
                    doFile.setFileMd5(fileMd5);

                    filename = autoRename(filePid, sessionDto.getUserId(), filename);
                    doFile.setFileName(filename);
                    this.fileInfoMapper.insert(doFile);

                    resultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
                    updateUserSpace(sessionDto, doFile.getFileSize());

                    return resultDto;
                }
            }

/**
 *        2，秒传判断失败，进行分片上传操作
 * */
            Long tempFileSzie = redisComponent.getTempFileSzie(sessionDto.getUserId(), fileId);
            if (tempFileSzie + file.getSize() > userSpace.getTotalSpace()) {
                throw new SystemException("网盘空间不足，上传失败");
            }

//            临时存储目录
            String tempfolder = appconfig.getProjectFolder() + HttpeCode.Tempfolder_UploadFile;
            //  wenzhuo TODO 2024/3/28 :待修改：由于不知为何每次前段传来的fileid都是“”，虽然相应体中返回1了对应的fileid，导致这里的缓存目录只能写死
            String curtempName = sessionDto.getUserId() + HttpeCode.tempFile + fileId;
            tempFile = new File(tempfolder + "/" + curtempName);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            File newFile = new File(tempFile.getPath() + "/" + chunkIndex);
            file.transferTo(newFile);
            redisComponent.UploadTempFileSize(sessionDto.getUserId(), fileId, file.getSize());
            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());
                return resultDto;
            }

            String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM.getPattern());
            String suffix = StringUtil.getFileSuffix(filename);
            String realFileName = curtempName + suffix;
            FileTypeEnums fileTypeEnums = FileTypeEnums.getFileTypeBySuffix(suffix);
            filename = autoRename(filePid, sessionDto.getUserId(), filename);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(fileId);
            fileInfo.setUserId(sessionDto.getUserId());
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFileName(filename);
            fileInfo.setFilePath(month + "/" + realFileName);
            fileInfo.setFilePid(filePid);
            fileInfo.setCreateTime(curtime);
            fileInfo.setLastUpdateTime(curtime);
            fileInfo.setFileCategory(fileTypeEnums.getCategory().getCategory());
            fileInfo.setFileType(fileTypeEnums.getType());
            fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());
            fileInfo.setFolderType(FileFolderTypeEnums.FILE.getType());
            fileInfo.setDelFlag(FileDefalgEnums.USING.getStatus());
            fileInfoMapper.insert(fileInfo);
            Long totalSize = redisComponent.getTempFileSzie(sessionDto.getUserId(), fileId);
            updateUserSpace(sessionDto, totalSize);
            resultDto.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());
            /**
             * 3，合成分片
             * */

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            transferFile(fileInfo.getFileId(), sessionDto);

                        }
                    }
            );


            //  wenzhuo TODO 2024/3/29 : 缩略图功能待完善


            return resultDto;
        } catch (IOException e) {
            log.error("文件写入失败，file.transferTo(newFile);");
            uploadStatas = false;

        } finally {
            if (!uploadStatas && tempFile != null) {
                try {
                    FileUtils.deleteDirectory(tempFile);
                } catch (IOException e) {
                    log.error("文件写入失败后，删除临时文件同样失败，file.transferTo(newFile);");
                    throw new RuntimeException(e);
                }

            }
        }

        return resultDto;
    }

    @Resource
    private UserInfoMapper userInfoMapper;


    /**
     * @return void
     * @Author wenzhuo4657
     * @Description 更新用户已经使用的空间
     * @Date 19:45 2024-03-28
     * @Param [sessionDto, fileSize]
     **/
    private void updateUserSpace(SessionDto sessionDto, Long fileSize) {
        Integer count = userInfoMapper.updateUserSpace(sessionDto.getUserId(), fileSize);
        if (count == 0) {
            throw new SystemException(ResponseEnum.CODE_904);
        }
        //同步到redis中
        UserSpace space = redisComponent.getUserSpaceUser(sessionDto.getUserId());
        space.setUsespace(space.getUsespace() + fileSize);
        redisComponent.saveUserid_space(sessionDto.getUserId(), space);

    }

    /**
     * @return java.lang.String
     * @Author wenzhuo4657
     * @Description 只有在该切片文件数量大于0时，才会对其进行重命名
     * @Date 20:26 2024-03-26
     * @Param [filePid, userId, filename]
     **/
    private String autoRename(String filePid, String userId, String filename) {
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setUserId(userId);
        fileInfoQuery.setFilePid(filePid);
        fileInfoQuery.setDelFlag(FileDefalgEnums.USING.getStatus());
        fileInfoQuery.setFileName(filename);
        Integer count = this.fileInfoMapper.selectCount(fileInfoQuery);
        if (count > 0) {
            filename = StringUtil.rename(filename);
        }
        return filename;
    }

    @Override
    public void transferFile(String fileId, SessionDto sessionDto) {
        Boolean transferSuccess = true;//转码结果状态
        String targetFilePath = null;//合并文件路径
        FileTypeEnums fileTypeEnums = null;//文件类型
        try {
            FileInfo fileInfo = fileInfoMapper.selectByFileidAndUserid(fileId, sessionDto.getUserId());


            String tempfolder = appconfig.getProjectFolder() + HttpeCode.Tempfolder_UploadFile;
            String curtempName = sessionDto.getUserId() + HttpeCode.tempFile + fileId;
            File tempFile = new File(tempfolder + "/" + curtempName);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            String suffix=StringUtil.getFileSuffix(fileInfo.getFileName());
            String month = DateUtil.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYY_MM.getPattern());
            String targetFolderName = appconfig.getProjectFolder() + "/"+HttpeCode.File_userid;
            File targetFolder = new File(targetFolderName + "/" + month);
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }

            String realFileName = curtempName + suffix;
            targetFilePath = targetFolder.getPath() + "/" + realFileName;

            union(tempFile.getPath(), targetFilePath, fileInfo.getFileName(), true);





        }catch (Exception e){
            logger.error("{}",e);
            throw  new SystemException("分片合并失败");
        }
    }

    /**
    * @Author wenzhuo4657
    * @Description 将指定位置的文件分片合并到新位置，
    * @Date 20:23 2024-03-29
    * @Param [
     * path, 分片文件目录
     * targetFilePath, 分片合并后存储的位置
     * fileName, 文件名称
     * b 是否删除分片文件]
    * @return void
    **/

    private void union(String path, String targetFilePath, String fileName, boolean c) {
        File dir = new File(path);
        //判断需要转码的文件目录是否存在
        if (!dir.exists()) {
            throw new SystemException(HttpeCode.UPLOAD_FOLDER_NOT_EXIT);
        }
        File[] fileList = dir.listFiles();
        //文件实际所在的目录，不是临时目录
        File targetFile = new File(targetFilePath);
        RandomAccessFile writeFile = null;
        try {
            //可读写
            writeFile = new RandomAccessFile(targetFile, "rw");
            //缓冲区
            byte[] b = new byte[1024 * 10];
            //遍历临时目录的所有分片文件
            for (int i=0; i<fileList.length; i++) {
                int len = -1;
                File chunkFile = new File(path + "/" + i);
                RandomAccessFile readFile = null;
                try {
                    //只读
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(b)) != -1) {
                        writeFile.write(b, 0, len);
                    }
                } catch (Exception e) {
                    //合并分片失败
                    throw new SystemException(HttpeCode.UPLOAD_MERGE_ONE_FAIL);
                } finally {
                    readFile.close();
                }
            }
            LambdaQueryWrapper<FileInfo> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(FileInfo::getFileId,"1");
            FileInfo fileInfo=new FileInfo();
            fileInfo.setFileSize(2222L);
            fileInfo.setStatus(FileStatusEnums.USING.getStatus());
            fileInfoMapper.update(fileInfo,wrapper);
        } catch (Exception e) {
            //合并文件失败
            logger.error("合并文件: {}失败{}", fileName, e);
            throw new SystemException(HttpeCode.UPLOAD_MERGE_FILE_FAIL);
        } finally {
            try {
                if (null != writeFile) {
                    writeFile.close();
                }
            } catch (IOException e) {
                log.error("关闭流失败", e);
                throw new SystemException(HttpeCode.UPLOAD_IO_CLOSE_FAIL);
            }
            if (c) {
                if (dir.exists()) {
                    try {
                        FileUtils.deleteDirectory(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
