package com.es.controller;

import com.es.pojo.dto.response.ProductMindSearchRes;
import com.es.service.ProductMindSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 *  @Description 商品联动搜索controller
 *  @author liuhu
 *  @Date 2022/1/10 21:39
 */
@RestController
public class ProductMindController {

    @Autowired
    private ProductMindSearchService productMindSearchService;

    /**
     * @Description 联动搜索
     * @author liuhu
     * @param word
     * @date 2022/1/10 21:39
     * @return java.util.List<com.es.pojo.dto.response.ProductMindSearchRes>
     */
    @GetMapping("mind")
    public List<ProductMindSearchRes> productMindSearch(@RequestParam(name = "word") String word){
       return productMindSearchService.productMindSearch(word);
    }
}
