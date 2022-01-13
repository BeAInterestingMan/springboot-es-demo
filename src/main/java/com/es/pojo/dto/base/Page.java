package com.es.pojo.dto.base;

import lombok.Data;

import java.util.List;

/**
 *  @Description 分页参数
 *  @author liuhu
 *  @Date 2021/12/22 22:21
 */
@Data
public class Page<T> {

    private Integer currentPage;

    private Integer pageSize;

    private Long total;

    private List<T> data;

    public Page(Integer currentPage, Integer pageSize, Long total, List<T> data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }
}
