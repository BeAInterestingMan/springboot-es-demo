package com.es.pojo.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
/**
 *  @Description 商品sku
 *  @author liuhu
 *  @Date 2022/1/10 19:59
 */
@Data
public class ProductSku {

    /**商品sku编码*/
    private String skuCode;

    /**商品名称*/
    private String productName;

    /**商品价格*/
    private BigDecimal price;

    /**商品描述*/
    private String description;

    /**商品主图*/
    private String mainPicture;

    /**商品类目集合*/
    private List<String> categoryCode;

    /**商品产地坐标*/
    private String location;

    /**商品可用库存*/
    private Long stock;

    /**商品品牌编码*/
    private String brandCode;

    /**商品品牌名称*/
    private String brandName;

    /**商品联想搜索文本*/
    private String suggestText;

    /**商品上下架状态  0-下架 1-上架*/
    private Integer status;

    /**创建时间*/
    private LocalDateTime createTime;

    /**修改时间*/
    private LocalDateTime updateTime;

    /**上架时间*/
    private LocalDateTime shelvesTime;
}
