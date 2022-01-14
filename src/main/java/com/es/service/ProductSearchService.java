package com.es.service;

import com.es.pojo.dto.ProductSkuDTO;
import com.es.pojo.dto.base.Page;
import com.es.pojo.dto.request.ProductHighLightSearchReq;
import com.es.pojo.dto.request.ProductRecommendSearchReq;
import com.es.pojo.dto.response.ProductCategoryRes;
import com.es.pojo.dto.response.ProductMindSearchRes;

import java.util.List;

/**
 *  @Description es搜索词连线
 *  @author liuhu
 *  @Date 2022/1/10 19:50
 */
public interface ProductSearchService {

   /**
    * @Description 商品联想搜索
    * @author liuhu
    * @param word
    * @date 2022/1/10 20:12
    * @return java.util.List<com.es.pojo.dto.response.ProductMindSearchRes>
    */
   List<ProductMindSearchRes> productMindSearch(String word);
   /**
    * @Description 获取分类下商品数量&分类code
    * @author liuhu
    * @param
    * @date 2022/1/12 10:30
    * @return java.util.List<com.es.pojo.dto.response.ProductCategoryRes>
    */
   List<ProductCategoryRes> productCategoryList();
   /**
    * @Description 商品推荐搜索  在A分类中推荐指定品牌和价格得商品  置顶
    * @author liuhu
    * @param recommendSearchReq
    * @date 2022/1/12 16:00
    * @return java.util.List<com.es.pojo.dto.ProductSkuDTO>
    */
   Page<List<ProductSkuDTO>> recommendProductSearch(ProductRecommendSearchReq recommendSearchReq);

   /**
    * @Description 商品搜索
    * @author liuhu
    * @param productSkuDTO
    * @date 2022/1/13 9:32
    * @return com.es.pojo.dto.base.Page<java.util.List<com.es.pojo.dto.ProductSkuDTO>>
    */
   Page<List<ProductSkuDTO>> productSearch(ProductSkuDTO productSkuDTO);

   void productHotWordSearch();

   /**
    * @Description 高亮搜索
    * @author liuhu
    * @param request
    * @date 2022/1/14 14:21
    * @return com.es.pojo.dto.base.Page<java.util.List<com.es.pojo.dto.ProductSkuDTO>>
    */
   Page<List<ProductSkuDTO>> productHighLightSearchSearch(ProductHighLightSearchReq request);
}
