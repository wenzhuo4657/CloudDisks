package cn.wenzhuo4657.mapper;

import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 文件信息(FileInfo)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-22 20:15:01
 */

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    Long selectAllByUserIdLong(@Param("id") String userId);
    /**
     *有一部分参数一部分模糊查询
     * @param param
     * @return 符合条件的记录数量
     */
    Integer selectCount(FileInfoQuery param);

    /**
    * @Author wenzhuo4657
    * @Description 有一部分参数模糊查询
    * @Date 19:21 2024-03-26
    * @Param [infoQuery]
    * @return java.util.List<cn.wenzhuo4657.domain.entity.FileInfo>
    **/
    List<FileInfo> findListByInfoQuery(FileInfoQuery infoQuery);
}
