package com.hqy.cloud.common.result;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 * @author qy
 * @date 2021-09-14
 */
@Setter
@Getter
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
     * 分页大小
     */
    private int pageSize;


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
        this.pageSize = pageSize;
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

}
