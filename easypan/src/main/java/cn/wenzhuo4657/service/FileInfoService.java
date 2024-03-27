package cn.wenzhuo4657.service;

import cn.wenzhuo4657.domain.dto.FileInfoDto;
import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.dto.SessionDto;
import cn.wenzhuo4657.domain.dto.UploadResultDto;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;


/**
 * 文件信息(FileInfo)表服务接口
 *
 * @author makejava
 * @since 2024-03-22 20:15:05
 */
public interface FileInfoService extends IService<FileInfo> {


    PaginationResultDto<FileInfoDto> findListBypage(FileInfoQuery query);

    UploadResultDto uploadFile(SessionDto sessionDto, String fileId, MultipartFile file, String filename, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);
}
