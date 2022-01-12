package com.es.service.impl;

import com.es.helper.RestClientHelper;
import com.es.pojo.dto.AggSearchBaseDTO;
import com.es.pojo.dto.ProductSkuDTO;
import com.es.pojo.dto.base.BaseEsPageRequest;
import com.es.pojo.dto.base.Page;
import com.es.pojo.dto.request.ProductRecommendSearchReq;
import com.es.pojo.dto.response.ProductCategoryRes;
import com.es.pojo.dto.response.ProductMindSearchRes;
import com.es.service.ProductSearchService;
import com.es.utils.CopyUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  @Description 商品联想搜索实现类
 *  @author liuhu
 *  @Date 2022/1/10 20:12
 */
@Service
public class ProductSearchServiceImpl implements ProductSearchService {
    @Autowired
    private RestClientHelper restClientHelper;

    @Override
    public List<ProductMindSearchRes> productMindSearch(String word) {
        if(StringUtils.isBlank(word)){
            return Lists.newArrayList();
        }
        return restClientHelper.queryMindSearch("suggestText",word,"mind_group");
    }

    @Override
    public List<ProductCategoryRes> productCategoryList() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 查询所有上架得商品
        boolQueryBuilder.must(QueryBuilders.termQuery("status", 1));
        // 对所有分类编码进行分组
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("category_group").field("categoryCode");
        List<AggSearchBaseDTO> categoryGroup = restClientHelper.aggSearch(boolQueryBuilder, aggregationBuilder, "category_group");
        return categoryGroup.stream().map(v -> {
            ProductCategoryRes productCategoryRes = new ProductCategoryRes();
            productCategoryRes.setCategoryCode(v.getKey());
            productCategoryRes.setCount(v.getCount());
            return productCategoryRes;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<List<ProductSkuDTO>> recommendProductSearch(ProductRecommendSearchReq recommendSearchReq) {
        // 搜索商品
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("productName",recommendSearchReq.getProductName()));
        // 评分过滤 命中指定分类加10分  命中指定品牌加5分
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("brandCode", recommendSearchReq.getBrandCode()),
                        ScoreFunctionBuilders.weightFactorFunction(5)),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termsQuery("categoryCode",recommendSearchReq.getCategoryCode()),
                        ScoreFunctionBuilders.weightFactorFunction(10))
        };
        BaseEsPageRequest baseEsPageRequest = CopyUtil.copyBean(recommendSearchReq, BaseEsPageRequest.class);
        return restClientHelper.scoreSearch(boolQueryBuilder,filterFunctionBuilders,baseEsPageRequest,ProductSkuDTO.class);
    }
}
