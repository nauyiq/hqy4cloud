package com.hqy.cloud.common.result;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 * @author qy
 * @date 2021-09-14
 */
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 4206093235632254490L;

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 总数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 结果集
     */
    private List<T> resultList = new ArrayList<>();

    public PageResult() {
    }

    /**
     * 根据分页条件和总数构造分页对象
     * @param currentPage 当前页
     * @param pageSize    每页的数据
     * @param total       总数
     * @param resultList  当前页数据集合
     */
    public PageResult(int currentPage, int pageSize, long total, List<T> resultList) {
        this.currentPage = currentPage;
        this.resultList = resultList;
        this.total = total;
        if (total == 0) {
            this.pages = 0;
        } else {
            this.pages = (int) (currentPage % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        }
    }

    public PageResult(int currentPage, long total, int pages, List<T> resultList) {
        this.currentPage = currentPage;
        this.total = total;
        this.pages = pages;
        this.resultList = resultList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void setResultList(List<T> resultList) {
        this.resultList = resultList;
    }
}
