package cn.wenzhuo4657.service.impl;

import cn.wenzhuo4657.domain.dto.FileInfoDto;
import cn.wenzhuo4657.domain.dto.PaginationResultDto;
import cn.wenzhuo4657.domain.entity.FileInfo;
import cn.wenzhuo4657.domain.query.FileInfoQuery;
import cn.wenzhuo4657.mapper.FileInfoMapper;
import cn.wenzhuo4657.service.FileInfoService;
import cn.wenzhuo4657.utils.BeancopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件信息(FileInfo)表服务实现类
 *
 * @author makejava
 * @since 2024-03-22 20:15:07
 */
@Service("fileInfoService")
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {



    /**
    * @Author wenzhuo4657
    * @Description
    * @Date 19:20 2024-03-23
    * @Param [query] 这里的query存储的数据 表示page查询的条件
    * @return cn.wenzhuo4657.domain.dto.PaginationResultDto
    **/
    @Override
    public PaginationResultDto<FileInfoDto> findListBypage(FileInfoQuery query) {

        Page page=new Page<>(query.getPageNo(),query.getPageSize());
        List<OrderItem> orders=new ArrayList<>();
        orders.add(OrderItem.desc(query.getOrderBy()));
        page.setOrders(orders);
        LambdaQueryWrapper<FileInfo> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getUserId,query.getUserId());
        wrapper.eq(FileInfo::getDelFlag,query.getDelFlag());
        wrapper.eq(FileInfo::getFileCategory,query.getFileCategory());
        IPage<FileInfo> page1=page(page,wrapper);

        PaginationResultDto<FileInfoDto> resultDto=new PaginationResultDto<>();
        resultDto.setList(BeancopyUtils.copyBeanList(page1.getRecords(), FileInfoDto.class));
        resultDto.setPageSize((int) page1.getSize());//分页大小
        resultDto.setTotalCount((int)page1.getTotal());//记录总条数
        resultDto.setPageNo((int)page1.getCurrent());//当前页数
        resultDto.setPageTotal((int)page1.getPages());//总页数

        return resultDto;
    }
}
