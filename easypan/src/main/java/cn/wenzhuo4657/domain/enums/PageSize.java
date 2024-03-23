package cn.wenzhuo4657.domain.enums;

/**
 * @author: 35238
 * 功能:
 * 时间: 2023-12-08 09:38
 */
public enum PageSize {
    SIZE15(15), SIZE20(20), SIZE30(30), SIZE40(40), SIZE50(50);
    int size;

    private PageSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }
}