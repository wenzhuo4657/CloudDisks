package cn.wenzhuo4657.domain.query;

import lombok.Data;

/**
 * @author: 35238
 * 功能:
 * 时间: 2023-12-08 09:40
 */
@Data
public class BaseParam {

    private SimplePage simplePage;

    private Integer pageNo;
    private Integer pageSize;
    private String orderBy;
    public SimplePage getSimplePage() {
        return simplePage;
    }
}