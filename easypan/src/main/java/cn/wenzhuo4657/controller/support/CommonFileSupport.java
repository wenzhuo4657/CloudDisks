package cn.wenzhuo4657.controller.support;

import cn.wenzhuo4657.config.redisComponent;
import cn.wenzhuo4657.domain.ResponseVo;
import cn.wenzhuo4657.domain.dto.DownloadFileDto;
import cn.wenzhuo4657.domain.dto.FileInfoDto;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.enums.*;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.utils.BeancopyUtils;
import cn.wenzhuo4657.utils.StringUtil;
import com.sun.deploy.net.HttpResponse;
import javafx.scene.shape.Path;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * @className: CommonFileSupport
 * @author: wenzhuo4657
 * @date: 2024/3/30 12:56
 * @Version: 1.0
 * @description:  对文件处理的一些支持方法
 */
public class CommonFileSupport extends  ControllerSupport{
    @Autowired
    private appconfig  appconfig;

/**
* @Author wenzhuo4657
* @Description
* @Date 13:16 2024-03-30
* @Param [response,
 * imageFloder, 注意，图片文件夹的表现形式是日期
 * imageName]
* @return void
**/
    protected void getImage(HttpServletResponse response, String imageFloder, String imageName) {
        if (StringUtil.isEmpty(imageFloder)||StringUtil.isEmpty(imageName)){
            return;
        }
        String filePath=appconfig.getProjectFolder()+ "/"+HttpeCode.File_userid+
                "/"+  imageFloder + "/" + imageName;
        String imageSuffix=StringUtil.getFileSuffix(imageName);
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }
    @Autowired
    private FileInfoMapper fileInfoMapper;

/**
* @Author wenzhuo4657
* @Description 返回视频预览文件，流程与前端接口有关，
* @Date 19:17 2024-03-31
* @Param [response, sessionDto, fileId]
* @return void
**/
    protected void getVideoFile(HttpServletResponse response, SessionDto sessionDto, String fileId) {
        //  wenzhuo TODO 2024/4/8 : md5相同的两个文件一个位于回收站，一个位于正常的网盘路径上时，无法打开视频预览
        if (StringUtil.isEmpty(fileId)){
            return;
        }
        if (fileId.endsWith(".ts")){
//            切片文件上传
            String realFileId=fileId.split("_")[0];
            FileInfo fileInfo=fileInfoMapper.selectByFileidAndUserid(realFileId,sessionDto.getUserId());
            if (Objects.isNull(fileInfo)||fileInfo.getFileType()!= FileTypeEnums.VIDEO.getType()){
                return;
            }
            String moth=fileInfo.getFilePath().split("/")[0];
            String path= appconfig.getProjectFolder()
                    +"/"+HttpeCode.File_userid
                    +"/"+moth
                    +"/"+sessionDto.getUserId()+HttpeCode.tempFile+realFileId
                    +"/"+fileId;
            File file=new File(path);
            if (!file.exists()){
                return;
            }
            readFile(response,path);

        }else {
//            切片文件
            FileInfo fileInfo=fileInfoMapper.selectByFileidAndUserid(fileId,sessionDto.getUserId());
            if (Objects.isNull(fileInfo)||fileInfo.getFileType()!= FileTypeEnums.VIDEO.getType()){
                return;
            }
            String moth=fileInfo.getFilePath().split("/")[0];
            String path="";
            if (fileInfo.getFileType()==FileTypeEnums.VIDEO.getType())
                path= appconfig.getProjectFolder()
                        +"/"+HttpeCode.File_userid
                        +"/"+moth
                        +"/"+sessionDto.getUserId()+HttpeCode.tempFile+fileId
                        +"/"+HttpeCode.M3U8_NAME;
            File file=new File(path);
            if (!file.exists()){
                return;
            }
            readFile(response,path);
        }

    }

    protected void getFile(HttpServletResponse response, SessionDto sessionDto, String fileId) {
        if (StringUtil.isEmpty(fileId)){
            return;
        }
            FileInfo fileInfo=fileInfoMapper.selectByFileidAndUserid(fileId,sessionDto.getUserId());
            if (Objects.isNull(fileInfo)){
                return;
            }

            String path= appconfig.getProjectFolder()
                        +"/"+HttpeCode.File_userid
                        +"/"+fileInfo.getFilePath();
            File file=new File(path);
            if (!file.exists()){
                return;
            }
            readFile(response,path);
        }

        /**
        * @Author wenzhuo4657
        * @Description  获取指定path路径下多级目录的信息封装为list返回
        * @Date 11:58 2024-04-02
        * @Param [sessionDto, path]
        * @return cn.wenzhuo4657.domain.ResponseVo
        **/
    protected ResponseVo getFolderInfo(SessionDto sessionDto, String path) {
        String [] array=path.split("/");
        String userId=sessionDto.getUserId();
        FileInfoQuery fileInfoQuery=new FileInfoQuery();
        fileInfoQuery.setFileIdArray(array);
        fileInfoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        fileInfoQuery.setUserId(userId);
        String OrderBy="field(file_id,\'"+ StringUtils.join(array,"\',\'") +"\')";
        fileInfoQuery.setOrderBy(OrderBy);
        List<FileInfo> list=fileInfoMapper.findListByInfoQuery(fileInfoQuery);
        return  ResponseVo.ok(BeancopyUtils.copyBeanList(list, FileInfoDto.class));

    }

    @Resource
    private redisComponent redisComponent;

    /**
    * @Author wenzhuo4657
    * @Description 创建下载链接
    * @Date 14:00 2024-04-03
    * @Param [fileId, userId]
    * @return cn.wenzhuo4657.domain.ResponseVo
    **/
    public ResponseVo createDownloadUrl(String fileId, String userId) {
        FileInfo fileInfo=fileInfoMapper.selectByFileidAndUserid(fileId,userId);
        if (fileInfo==null){
            throw  new SystemException(ResponseEnum.CODE_600);
        }
        if (fileInfo.getFolderType().equals(FileFolderTypeEnums.FOLDER.getType())){
            throw  new SystemException(ResponseEnum.CODE_600);
        }
        String code=StringUtil.getRandom_Number(HttpeCode.code_length_50);
        DownloadFileDto dto=new DownloadFileDto();
        dto.setFileId(code);
        dto.setFileName(fileInfo.getFileName());
        dto.setFilePath(fileInfo.getFilePath());
        dto.setDownloadCode(code);

        redisComponent.saveDownloadCode(code, dto);
        return  ResponseVo.ok(code);

    }





}
