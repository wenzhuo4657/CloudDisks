package cn.wenzhuo4657.domain.query;

import cn.wenzhuo4657.domain.enums.PageSize;
import lombok.Data;

/**
 * @author: 35238
 * 功能:对结果集进行限定
 * 时间: 2023-12-08 09:36
 */
@Data
public class SimplePage {
    private int pageNo;
    private int countTotal;//总记录长度
    private int pageSize;//页的大小
    private int pageTotal;//总页数


//    start、end表示限定了表示返回结果的长度和位置，在sql语句中使用为
//    limit #{simplePage.start},#{simplePage.end}
    private int start;
    private int end;

    public SimplePage() {
    }

    public SimplePage(Integer pageNo, int countTotal, int pageSize) {
        if (null == pageNo) {
            pageNo = 0;
        }
        this.pageNo = pageNo;
        this.countTotal = countTotal;
        this.pageSize = pageSize;
        action();
    }

    public SimplePage(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public void action() {
        if (this.pageSize <= 0) {
            this.pageSize = PageSize.SIZE20.getSize();
        }
        if (this.countTotal > 0) {
            this.pageTotal = this.countTotal % this.pageSize == 0 ? this.countTotal / this.pageSize
                    : this.countTotal / this.pageSize + 1;
        } else {
            pageTotal = 1;
        }

        if (pageNo <= 1) {
            pageNo = 1;
        }
        if (pageNo > pageTotal) {
            pageNo = pageTotal;
        }
        this.start = (pageNo - 1) * pageSize;
        this.end = this.pageSize;
    }
}