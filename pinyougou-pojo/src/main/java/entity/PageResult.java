package entity;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {
    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private List<T> dataList;
    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    private Integer pageCount;

    public PageResult() {}

    public PageResult(List<T> dataList) {
        this(dataList, DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE);
    }

    public PageResult(List<T> dataList, Integer pageNum, Integer pageSize) {
        this.pageNum = (pageNum < 1) ? DEFAULT_PAGE_NUM : pageNum;
        this.pageSize = (pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        this.total = dataList.size();
        this.initPageResult(dataList);
    }

    private void initPageResult(List<T> dataList) {
        List<T> subDataList;
        int fromIndex;
        int toIndex;

        if ((this.pageNum - 1) * this.pageSize >= this.total) {
            fromIndex = this.total - this.total % this.pageSize;
        } else {
            fromIndex = (this.pageNum - 1) * this.pageSize;
        }

        if (fromIndex + this.pageSize > this.total) {
            toIndex = this.total;
        } else {
            toIndex = fromIndex + this.pageSize;
        }

        if (this.total % this.pageSize == 0) {
            this.pageCount = this.total / this.pageSize;
        } else {
            this.pageCount = this.total / this.pageSize + 1;
        }

        subDataList = dataList.subList(fromIndex, toIndex);
        this.dataList = subDataList;
    }

    public List<T> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public Integer getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public String toString() {
        return "PageResult{" +
            "dataList=" + this.dataList +
            ", pageNum=" + this.pageNum +
            ", pageSize=" + this.pageSize +
            ", total=" + this.total +
            ", pageCount=" + this.pageCount +
            '}';
    }
}
