package com.es.service.impl;

import com.es.helper.RestClientHelper;
import com.es.pojo.dto.response.ProductMindSearchRes;
import com.es.service.ProductMindSearchService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  @Description 商品联想搜索实现类
 *  @author liuhu
 *  @Date 2022/1/10 20:12
 */
@Service
public class ProductMindSearchServiceImpl implements ProductMindSearchService  {
    @Autowired
    private RestClientHelper restClientHelper;

    @Override
    public List<ProductMindSearchRes> productMindSearch(String word) {
        if(StringUtils.isBlank(word)){
            return Lists.newArrayList();
        }
        return restClientHelper.queryMindSearch("suggestText",word);
    }
}
