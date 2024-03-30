package cn.wenzhuo4657.controller.support;

import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.enums.appconfig;
import cn.wenzhuo4657.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;

/**
 * @className: CommonFileSupport
 * @author: wenzhuo4657
 * @date: 2024/3/30 12:56
 * @Version: 1.0
 * @description:
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
}