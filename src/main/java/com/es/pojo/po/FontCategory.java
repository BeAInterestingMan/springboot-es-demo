package com.es.pojo.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *  @Description 前台分类
 *  @author liuhu
 *  @Date 2022/1/10 19:59
 */
@Data
public class FontCategory {

    /**商品sku编码*/
    private String categoryCode;

    /**商品名称*/
    private String categoryName;

    /**分类类型 1-一级分类 2-二级分类 3-三级分类*/
    private String categoryType;

    private LocalDateTime  createTime;

    /**修改时间*/
    private LocalDateTime updateTime;

    /**商品上下架状态  0-下架 1-上架*/
    private Integer status;
}
