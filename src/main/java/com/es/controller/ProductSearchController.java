package com.es.controller;

import com.es.pojo.dto.ProductSkuDTO;
import com.es.pojo.dto.base.Page;
import com.es.pojo.dto.request.ProductHighLightSearchReq;
import com.es.pojo.dto.request.ProductRecommendSearchReq;
import com.es.pojo.dto.response.ProductCategoryRes;
import com.es.pojo.dto.response.ProductMindSearchRes;
import com.es.service.ProductSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 *  @Description 商品联动搜索controller
 *  @author liuhu
 *  @Date 2022/1/10 21:39
 */
@RestController
@RequestMapping("product")
public class ProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    /**
     * @Description 联动搜索(suggest)
     * @author liuhu
     * @param word
     * @date 2022/1/10 21:39
     * @return java.util.List<com.es.pojo.dto.response.ProductMindSearchRes>
     */
    @GetMapping("mind")
    public List<ProductMindSearchRes> productMindSearch(@RequestParam(name = "word") String word){
       return productSearchService.productMindSearch(word);
    }


    /**
     * @Description 查询商品得全部分类(聚合搜索 agg)
     * @author liuhu
     * @param
     * @date 2022/1/12 10:07
     * @return void
     */
    @GetMapping("categoryList")
    public List<ProductCategoryRes> productCategoryList(){
        return productSearchService.productCategoryList();
    }



    /**
     * @Description 商品推荐
     * @author liuhu
     * @param recommendSearchReq
     * @date 2022/1/12 16:51
     * @return com.es.pojo.dto.base.Page<java.util.List<com.es.pojo.dto.ProductSkuDTO>>
     */
    @PostMapping("recommend")
    public Page<List<ProductSkuDTO>> recommendProductSearch(@RequestBody ProductRecommendSearchReq recommendSearchReq){
        return productSearchService.recommendProductSearch(recommendSearchReq);
    }

    /**
     * @Description 商品普通搜索 分词以及一些其他的
     * @author liuhu
     * @param productSkuDTO
     * @date 2022/1/12 16:51
     * @return com.es.pojo.dto.base.Page<java.util.List<com.es.pojo.dto.ProductSkuDTO>>
     */
    @PostMapping("search")
    public Page<List<ProductSkuDTO>> productSearch(@RequestBody ProductSkuDTO productSkuDTO){
       return productSearchService.productSearch(productSkuDTO);
    }


    @PostMapping("hotWord")
    public Page<List<ProductSkuDTO>> productHotWordSearch(){
        //思路1 前端埋点将用户搜索关键字时间等信息写入es另一个索引
        // 2.用这个索引做数据分析 热词统计
        return null;
    }

    /**
     * @Description  高亮搜索
     * @author liuhu
     * @param request
     * @date 2022/1/14 21:10
     * @return com.es.pojo.dto.base.Page<java.util.List<com.es.pojo.dto.ProductSkuDTO>>
     */
    @PostMapping("highLightSearch")
    public Page<List<ProductSkuDTO>> productHighLightSearchSearch(@RequestBody ProductHighLightSearchReq request){
        return productSearchService.productHighLightSearchSearch(request);
    }
}
