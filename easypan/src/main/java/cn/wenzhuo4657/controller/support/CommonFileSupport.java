package cn.wenzhuo4657.controller.support;

import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.enums.FileTypeEnums;
import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.enums.appconfig;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.utils.StringUtil;
import com.sun.deploy.net.HttpResponse;
import javafx.scene.shape.Path;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
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

}
