package com.es.service;

import com.es.pojo.dto.response.ProductMindSearchRes;

import java.util.List;

/**
 *  @Description es搜索词连线
 *  @author liuhu
 *  @Date 2022/1/10 19:50
 */
public interface ProductMindSearchService {

   /**
    * @Description 商品联想搜索
    * @author liuhu
    * @param word
    * @date 2022/1/10 20:12
    * @return java.util.List<com.es.pojo.dto.response.ProductMindSearchRes>
    */
   List<ProductMindSearchRes> productMindSearch(String word);
}
