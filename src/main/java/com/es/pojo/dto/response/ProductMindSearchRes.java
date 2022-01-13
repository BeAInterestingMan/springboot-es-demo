package com.es.pojo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  @Description 商品搜索联想返回值
 *  @author liuhu
 *  @Date 2022/1/10 20:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMindSearchRes {

    /**联想结果*/
    private String content;

    /**skuCode*/
    private String skuCode;
}
