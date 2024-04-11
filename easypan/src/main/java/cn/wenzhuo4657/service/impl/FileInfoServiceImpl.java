package cn.wenzhuo4657.service.impl;

import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.domain.dto.*;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.entity.UserInfo;
import cn.wenzhuo4657.domain.enums.*;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.domain.query.SimplePage;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.mapper.UserInfoMapper;
import cn.wenzhuo4657.service.FileInfoService;
import cn.wenzhuo4657.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                fileId = StringUtil.getRandom_Number(HttpeCode.code_length);
            }
            resultDto.setFileId(fileId);
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
                    if (doFile.getFileSize() + userSpace.getUseSpace() > userSpace.getTotalSpace()) {
                        throw new SystemException(ResponseEnum.CODE_904);
                    }
                    doFile.setFileId(fileId);
                    doFile.setFilePid(filePid);
                    doFile.setUserId(sessionDto.getUserId());
                    doFile.setCreateTime(curtime);
                    doFile.setLastUpdateTime(curtime);
                    doFile.setStatus(FileStatusEnums.USING.getStatus());
                    doFile.setDelFlag(FileDefalgEnums.USING.getStatus());
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
        space.setUseSpace(space.getUseSpace() + fileSize);
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


    /**
    * @Author wenzhuo4657
    * @Description 对分片文件进行转码合并，且设置缩略图
     * 缩略图说明：
     * 如果是图片，则将文件后缀"."替换为“._”生成图片作为缩略图，
     * 如果是视频，生成切片（技术使用ffmtp），并设置缩略图
    * @Date 10:43 2024-03-30
    * @Param [fileId, sessionDto]
    * @return void
    **/
    @Override
    public void transferFile(String fileId, SessionDto sessionDto) {
        Boolean transferSuccess = true;//转码结果状态
        String targetFilePath = null;//合并文件路径
        FileTypeEnums fileTypeEnums = null;//文件类型
        String cover=null;//缩略图地址
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
            FileTypeEnums.getFileTypeBySuffix(suffix);
            fileTypeEnums=FileTypeEnums.getFileTypeBySuffix(suffix);

            if (fileTypeEnums==FileTypeEnums.VIDEO){
                cutFileType_Video(fileId,targetFilePath);
                cover=month+"/"+curtempName+HttpeCode.video+HttpeCode.img_png;
                String coverpath=targetFolderName+"/"+cover;
                ScaleFilter.createCover4Video(new File(targetFilePath), HttpeCode.LENGTH_150, new File(coverpath));
            }else if (FileTypeEnums.IMAGE==fileTypeEnums){
                cover=month + "/" + realFileName.replace(".", "_.");
                String coverpath = targetFolderName+"/"+cover;
                Boolean created = ScaleFilter.createThumbnailWidthFFmpeg(new File(targetFilePath), HttpeCode.LENGTH_150, new File(coverpath), false);
                if (!created){
                    FileUtils.copyFile(new File(targetFilePath), new File(cover));
                }
            }


            LambdaQueryWrapper<FileInfo> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(FileInfo::getFileId,fileId);
            FileInfo fileInfo1=new FileInfo();
            fileInfo1.setFileSize(Files.size(Paths.get(targetFilePath)));
            fileInfo1.setFileCover(cover);
            fileInfo1.setStatus(FileStatusEnums.USING.getStatus());
            fileInfoMapper.update(fileInfo1,wrapper);




        }catch (Exception e){
            logger.error("{}",e);
            throw  new SystemException("分片合并失败");
        }
    }

    private void cutFileType_Video(String fileId, String targetFilePath) {
        File tsFolder=new File(targetFilePath.substring(0,targetFilePath.lastIndexOf(".")));
        if (!tsFolder.exists()){
            tsFolder.mkdirs();
        }
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = tsFolder + "/" + HttpeCode.TS_NAME;
        String cmd = String.format(CMD_TRANSFER_2TS, targetFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, false);
        cmd = String.format(CMD_CUT_TS, tsPath, tsFolder.getPath() + "/" + HttpeCode.M3U8_NAME, tsFolder.getPath(), fileId);
        ProcessUtils.executeCommand(cmd, false);
        new File(tsPath).delete();
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

    @Override
    public FileInfo newFolder(String fileName, String filePid, SessionDto userInfofromSession) {
        checkName(fileName,filePid,userInfofromSession.getUserId());
        FileInfo fileInfo=new FileInfo();
        fileInfo.setFileId(StringUtil.getRandom_Number(HttpeCode.code_length_10));
        fileInfo.setFileName(fileName);
        fileInfo.setFilePid(filePid);
        fileInfo.setUserId(userInfofromSession.getUserId());
        fileInfo.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        Date date=new Date();
        fileInfo.setCreateTime(date);
        fileInfo.setLastUpdateTime(date);
        fileInfo.setDelFlag(FileDefalgEnums.USING.getStatus());
        fileInfo.setStatus(FileStatusEnums.USING.getStatus());
        fileInfoMapper.insert(fileInfo);
        return fileInfo;
    }

    /**
    * @Author wenzhuo4657
    * @Description 检查当前用户的同级目录有否有相同名称的文件夹或者文件，
     * ps:由于此处文件后缀未做隐藏处理，所以不必区分文件夹和文件名称的校验
    * @Date 20:04 2024-03-31
    * @Param [fileName, filePid, userId]
    * @return void
    **/
    private void checkName(String fileName, String filePid, String userId) {
        FileInfoQuery fileInfoQuery=new FileInfoQuery();
        fileInfoQuery.setFileName(fileName);
        fileInfoQuery.setFileId(filePid);
        fileInfoQuery.setUserId(userId);
        int count=fileInfoMapper.selectCount(fileInfoQuery);
        if (count>0){
            throw  new SystemException("该名称已存在");
        }
    }



    /**
    * @Author wenzhuo4657
    * @Description 对数据库中的文件/目录进行重命名
    * @Date 18:44 2024-04-02
    * @Param [fileId, fileName, userId]
    * @return cn.wenzhuo4657.domain.entity.FileInfo
    **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo rename(String fileId, String fileName, String userId) {
        FileInfo fileInfo=this.fileInfoMapper.selectByFileidAndUserid(fileId,userId);
        if (fileInfo==null){
            throw new SystemException("文件不存在");
        }
        String rename=fileName;
        if (FileFolderTypeEnums.FILE.getType().equals(fileInfo.getFolderType())){
            rename+=StringUtil.getFileSuffix(fileInfo.getFileName());
        }
        Date date=new Date();
        FileInfo da=new FileInfo();
        da.setLastUpdateTime(date);
        da.setFileName(rename);
        da.setFileId(fileId);
        this.fileInfoMapper.updateById(da);
        return da;
    }

    @Override
    public void changeFileFolder(String fileIds, String filePid, String userId) {
        String [] arr=fileIds.split(",");
        if (fileIds.equals(filePid)){
            throw  new SystemException(ResponseEnum.CODE_600);
        }

       FileInfo fileinfo_filePid=fileInfoMapper.selectByFileidAndUserid(filePid,userId);
        if (Objects.isNull(fileinfo_filePid)
                ||fileinfo_filePid.getFolderType()!=FileFolderTypeEnums.FOLDER.getType()
                ||fileinfo_filePid.getDelFlag()!=FileDefalgEnums.USING.getStatus()){
            throw  new SystemException(ResponseEnum.CODE_600);
        }
        FileInfoQuery fileInfoQuery=new FileInfoQuery();
        fileInfoQuery.setFileIdArray(arr);
        fileInfoQuery.setUserId(userId);
        List<FileInfo> list=fileInfoMapper.findListByInfoQuery(fileInfoQuery);
        if (list.size()==0){
            logger.info("出现了一次无效的文件转移请求");
            return;
        }
        Map<String ,FileInfo> map=list.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(),(da1,da2)->da2));
        FileInfoQuery fileInfoQuery1=new FileInfoQuery();
        fileInfoQuery1.setUserId(userId);
        fileInfoQuery1.setFilePid(filePid);
        List<FileInfo> fileInfos=fileInfoMapper.findListByInfoQuery(fileInfoQuery1);


//        检查是否出现重命名
        //  wenzhuo TODO 2024/4/2 : 这里遇到同名文件直接自动重命令，如果要写前端的话，这块需要改动将重命名交给用户

        fileInfos.stream().forEach(o->{
            if (map.containsKey(o.getFileName())){
                FileInfo info=map.get(o.getFileName());
                info.setFileName(StringUtil.getFileName_Suffix(info.getFileName())+"_"
                        +StringUtil.getRandom_Number(HttpeCode.code_length)
                        +StringUtil.getFileSuffix(info.getFileName())
                );
                map.remove(o.getFileName());
                map.put(info.getFileName(),info);
            }

        });
        Date date=new Date();
        map.values().stream().forEach(
                o->{
                    o.setFilePid(filePid);
                    o.setLastUpdateTime(date);
                    fileInfoMapper.updateById(o);
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFile(String userId, String fileIds) {
        String[] fileArray=fileIds.split(",");
        FileInfoQuery query=new FileInfoQuery();
        query.setFileIdArray(fileArray);
        query.setUserId(userId);
        query.setDelFlag(FileDefalgEnums.USING.getStatus());
        List<FileInfo> list=fileInfoMapper.findListByInfoQuery(query);
        if (list.isEmpty()){
            return;
        }

        List<String> delFolder=new ArrayList<>();//记录所有要删除的目录id
        for (FileInfo info:list){
            if (FileFolderTypeEnums.FOLDER.getType().equals(info.getFolderType()))
                findsubdirectory(delFolder,userId,info.getFileId(),FileDefalgEnums.USING.getStatus());
        }

        if (!delFolder.isEmpty()){
            FileInfo info=new FileInfo();
            info.setDelFlag(FileDefalgEnums.DEL.getStatus());
            fileInfoMapper.updateFileDelFlagBatch(info,userId,delFolder,null, FileDefalgEnums.USING.getStatus());
//删除所有目录id下的文件/文件夹，且由于第一层文件夹没有他的父级id,所以没有删除掉第一层
        }

//        这里将fileid中删除的文件都设置为回收站，便于找回，
        List<String> delFileid=Arrays.asList(fileArray);
        FileInfo info=new FileInfo();
        info.setRemoveTime(new Date());
        info.setDelFlag(FileDefalgEnums.RECYCLE.getStatus());
        fileInfoMapper.updateFileDelFlagBatch(info,userId,null,delFileid,FileDefalgEnums.USING.getStatus());
    }

    private void findsubdirectory(List<String> delFolder, String userId, String fileId, Integer Defalg) {
        delFolder.add(fileId);
        FileInfoQuery query=new FileInfoQuery();
        query.setDelFlag(Defalg);
        query.setUserId(userId);
        query.setFilePid(fileId);
        query.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        List<FileInfo> list=fileInfoMapper.findListByInfoQuery(query);
        for(FileInfo info:list){
            findsubdirectory(delFolder,userId,info.getFileId(),Defalg);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverFileBatch(String userId, String fileIds) {
        String[]  fileArray=fileIds.split(",");
        FileInfoQuery query=new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(fileArray);
        query.setDelFlag(FileDefalgEnums.RECYCLE.getStatus());
        List<FileInfo> list=fileInfoMapper.findListByInfoQuery(query);
        if (list.isEmpty()){
            return;
        }
        List<String> delFolder=new ArrayList<>();//记录所有要恢复的目录id
        for (FileInfo info:list){
            if (FileFolderTypeEnums.FOLDER.getType().equals(info.getFolderType()))
                findsubdirectory(delFolder,userId,info.getFileId(),FileDefalgEnums.DEL.getStatus());
        }

        if (!delFolder.isEmpty()){
            FileInfo info=new FileInfo();
            info.setDelFlag(FileDefalgEnums.USING.getStatus());
            fileInfoMapper.updateFileDelFlagBatch(info,userId,delFolder,null, FileDefalgEnums.DEL.getStatus());
        }


        List<String> delFileid=Arrays.asList(fileArray);
        FileInfo info=new FileInfo();
        info.setRemoveTime(new Date());
        info.setDelFlag(FileDefalgEnums.USING.getStatus());
        fileInfoMapper.updateFileDelFlagBatch(info,userId,null,delFileid,FileDefalgEnums.RECYCLE.getStatus());
    }
/**
* @Author wenzhuo4657
* @Description
* @Date 14:41 2024-04-10
* @Param [userId, fileIds,
 * admin 表示删除用户是否为管理员，如果是则在删除时无需进行判断文件状态，可强制删除，如果不是则需要判断文件状态删除，该判断是指在sql拼接中
 * ]
* @return void
**/
    @Override
    public void delFileBatch(String userId, String fileIds, boolean admin) {

        String[]  fileArray=fileIds.split(",");
        FileInfoQuery query=new FileInfoQuery();
        query.setUserId(userId);
        query.setFileIdArray(fileArray);
        query.setDelFlag(FileDefalgEnums.RECYCLE.getStatus());
        List<FileInfo> list=fileInfoMapper.findListByInfoQuery(query);
        if (list.isEmpty()){
            return;
        }

        List<String> delFolder=new ArrayList<>();//记录所有要彻底删除的目录id
        for (FileInfo info:list){
            if (FileFolderTypeEnums.FOLDER.getType().equals(info.getFolderType()))
                findsubdirectory(delFolder,userId,info.getFileId(),FileDefalgEnums.DEL.getStatus());
        }

        if (!delFolder.isEmpty()){
            fileInfoMapper.delFileDelFlagBatch(userId,delFolder,null,admin?null:FileDefalgEnums.DEL.getStatus());
        }
        fileInfoMapper.delFileDelFlagBatch(userId,null,Arrays.asList(fileArray),admin?null:FileDefalgEnums.RECYCLE.getStatus());

        //数据库中更新用户空间
        Long useSpace=fileInfoMapper.selectAllByUserIdLong(userId);
        userInfoMapper.updateUserSpace(userId,useSpace);

        //更新redis
        UserSpace userSpace= redisComponent.getUserSpaceUser(userId);
        userSpace.setUseSpace(useSpace);
        redisComponent.saveUserid_space(userId,userSpace);


    }
}
