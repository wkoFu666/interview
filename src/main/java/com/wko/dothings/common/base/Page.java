package com.wko.dothings.common.base;

import java.io.Serializable;

public class Page implements Serializable {

    private static final long	serialVersionUID	= 4594071922809227379L;

    /**
     * 默认页码
     */
    public static final int		DEFAULT_PAGE		= 1;

    /**
     * 默认分页大小
     */
    public static final int		DEFAULT_PAGE_SIZE	= 20;

    // 页码，默认为1
    private int pageNo;

    // 分页大小
    private int pageItems;

    private int totalRows;

    private String sortName;

    private String sortOrder;

    public void setPageNo(int pageNo) {
        this.pageNo = Math.max(pageNo, 1);
    }

    public void setPageItems(int pageItems) {
        this.pageItems = Math.max(pageItems, 1);
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = Math.max(totalRows, 0);

        // 去除分页的潜在错误（由于删了数据/修改了查询条件导致实际页数减小，例如：删除了最后一页的全部数据）
        // 求实际页数
        int realPageCount = totalRows % pageItems == 0 ? totalRows / pageItems : totalRows / pageItems + 1;

        if (pageNo > realPageCount) {
            // 修正当前页，最近的
            if (pageNo - 1 > pageNo - realPageCount) {
                pageNo = realPageCount;
            } else {
                pageNo = 1;
            }
        }
    }

    public int getPageNo() {
        return pageNo;
    }

    public int getPageItems() {
        return pageItems;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString(){
        return "Page{" +
                "pageNo=" + pageNo +
                ", pageItems=" + pageItems +
                ", totalRows=" + totalRows +
                ", sortName='" + sortName + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}
