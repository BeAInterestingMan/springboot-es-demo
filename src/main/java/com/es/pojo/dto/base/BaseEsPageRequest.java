package com.es.pojo.dto.base;

import lombok.Data;

import java.util.List;

/**
 * @author liuhu
 * @Description es查询请求入参
 * @Date 2022/1/10 19:36
 */
@Data
public class BaseEsPageRequest {

    private Integer currentPage = 1;

    private Integer pageSize = 10;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 排序字段
     */
    private List<EsSortParamDTO> esSortParamList;
}
