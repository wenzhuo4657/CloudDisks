package cn.wenzhuo4657.service;

import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.entity.FileShare;
import cn.wenzhuo4657.domain.query.FileShareQuery;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * (FileShare)表服务接口
 *
 * @author makejava
 * @since 2024-04-10 18:32:45
 */
public interface FileShareService extends IService<FileShare> {

    PaginationResultDto findListBypage(FileShareQuery query);

    void saveShare(FileShare share);

    void delFileShareBatch(String userId, String[] fileIdArray);
}
