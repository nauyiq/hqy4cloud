package com.hqy.fundation.common.result;

import java.io.Serializable;
import java.util.List;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-14 19:49
 */
public class PageResult<T> implements Serializable {

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
    private List<T> resultList;

    public PageResult() {
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
