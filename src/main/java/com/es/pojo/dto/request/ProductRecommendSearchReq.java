package com.es.pojo.dto.request;

import com.es.pojo.dto.base.BaseEsPageRequest;
import lombok.Data;

import java.util.List;
/**
 *  @Description 推荐搜索入参
 *  @author liuhu
 *  @Date 2022/1/12 15:56
 */
@Data
public class ProductRecommendSearchReq extends BaseEsPageRequest {

    /**商品sku编码*/
    private String skuCode;

    /**商品类目集合*/
    private List<String> categoryCode;

    /**商品可用库存*/
    private Long stock;

    /**商品可用库存*/
    private String productName;

    /**商品品牌编码*/
    private String brandCode;

}
