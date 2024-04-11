package cn.wenzhuo4657.mapper;

import cn.wenzhuo4657.domain.entity.FileShare;
import cn.wenzhuo4657.domain.query.FileShareQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * (FileShare)表数据库访问层
 *
 * @author makejava
 * @since 2024-04-10 18:32:41
 */
@Mapper
public interface FileShareMapper extends BaseMapper<FileShare> {

    Integer selectCount(FileShareQuery param);
    List<FileShare> selectListByQuery(FileShareQuery param);

    int delBatch(String userId, String[] fileIdArray);
}
