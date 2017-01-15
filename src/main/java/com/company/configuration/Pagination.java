package com.company.configuration;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Created by Administrator on 4/9/2016.
 */
public class Pagination implements Pageable {

    private int page;
    private int pageSize;
    private Sort sort;

    public Pagination page(int page) {
        this.page = page;
        return this;
    }

    public Pagination have (int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Pagination sortBy(Sort sort) {
        this.sort = sort;
        return this;
    }

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }


}
