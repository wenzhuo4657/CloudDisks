package cn.wenzhuo4657.mapper;

import cn.wenzhuo4657.domain.entity.FileInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * 文件信息(FileInfo)表数据库访问层
 *
 * @author makejava
 * @since 2024-03-22 20:15:01
 */

@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

}
