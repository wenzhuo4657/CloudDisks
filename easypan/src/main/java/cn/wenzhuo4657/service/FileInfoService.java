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


     void transferFile(String fileId, SessionDto sessionDto) ;

    PaginationResultDto<FileInfoDto> findListBypage(FileInfoQuery query);

    UploadResultDto uploadFile(SessionDto sessionDto, String fileId, MultipartFile file, String filename, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

    FileInfo newFolder(String fileName, String filePid, SessionDto userInfofromSession);

    FileInfo rename(String fileId, String fileName, String userId);

    void changeFileFolder(String fileIds, String filePid, String userId);

    void removeFile(String userId, String fileIds);

    void recoverFileBatch(String userId, String fileIds);

    void delFileBatch(String userId, String fileIds, boolean admin);

    void saveShare(String fileId, String shareFileIds, String myFolderId, String shareUserId, String userId);
}
