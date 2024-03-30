package cn.wenzhuo4657.controller.support;

import cn.wenzhuo4657.domain.enums.HttpeCode;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.exception.SystemException;
import cn.wenzhuo4657.utils.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

/**
 * @className: ControllerSupport
 * @author: wenzhuo4657
 * @date: 2024/3/21 11:56
 * @Version: 1.0
 * @description:
 */

public class ControllerSupport {

    private Logger logger= LoggerFactory.getLogger(ControllerSupport.class);

    /**
    * @Author wenzhuo4657
    * @Description  将filePath指向的文件传入response中
    * @Date 18:57 2024-03-21
    * @Param [response, filePath]
    * @return void
    **/

    public void readFile(HttpServletResponse response, String filePath) {
        if (!StringUtil.pathIsOk(filePath)) {
            return;
        }
        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len = 0;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception e) {
            throw new SystemException(e.toString());
        }
    }

    /**
    * @Author wenzhuo4657
    * @Description 从session中取出登录时（http://localhost:7090/api/login）存入的数据
    * @Date 19:00 2024-03-21
    * @Param [session]
    * @return cn.wenzhuo4657.domain.dto.SessionDto
    **/

    protected SessionDto getUserInfofromSession(HttpSession session){
        SessionDto sessionDto=(SessionDto)  session.getAttribute(HttpeCode.SessionDto_key);
        return  sessionDto;
    }

}