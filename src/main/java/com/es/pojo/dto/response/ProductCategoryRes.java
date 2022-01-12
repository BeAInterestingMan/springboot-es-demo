package com.es.pojo.dto.response;

import lombok.Data;
/**
 *  @Description 商品分类查询
 *  @author liuhu
 *  @Date 2022/1/12 10:30
 */
@Data
public class ProductCategoryRes {
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 分类code
     */
    private String categoryCode;
    /**
     * 分类商品数量
     */
    private Long count;
}
