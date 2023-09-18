package com.eneamathos.dscwebapi.Common;

import org.springframework.data.domain.Sort;

public class PageModel {
    private int pageNumber = 0;
    private int pageSize = 10;
    private String[] sortBy = {"id", "desc"};

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String[] getSortBy() {
        return sortBy;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setSortBy(String[] sortBy) {
        this.sortBy = sortBy;
    }
}
