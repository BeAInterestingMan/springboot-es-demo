package com.es.pojo.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *  @Description 商品品牌
 *  @author liuhu
 *  @Date 2022/1/10 19:59
 */
@Data
public class Brand {

    /**商品sku编码*/
    private String brandCode;

    /**商品名称*/
    private String brandName;

    /**商品价格*/
    private String description;

    private LocalDateTime  createTime;

    /**修改时间*/
    private LocalDateTime updateTime;

    /**商品上下架状态  0-下架 1-上架*/
    private Integer status;
}
