package com.es.service.impl;

import com.es.enums.IndexEnum;
import com.es.helper.RestClientHelper;
import com.es.pojo.dto.AggSearchBaseDTO;
import com.es.pojo.dto.ProductSkuDTO;
import com.es.pojo.dto.base.BaseEsPageRequest;
import com.es.pojo.dto.base.EsSortParamDTO;
import com.es.pojo.dto.base.Page;
import com.es.pojo.dto.request.ProductHighLightSearchReq;
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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Override
    public Page<List<ProductSkuDTO>> productSearch(ProductSkuDTO productSkuDTO) {
        BoolQueryBuilder boolQueryBuilder = buildQuery(productSkuDTO);
        BaseEsPageRequest baseEsPageRequest = buildPageParam();
        return restClientHelper.queryPage(boolQueryBuilder,baseEsPageRequest,ProductSkuDTO.class);
    }

    @Override
    public void productHotWordSearch() {

    }

    @Override
    public Page<List<ProductSkuDTO>> productHighLightSearchSearch(ProductHighLightSearchReq request) {
        if(StringUtils.isBlank(request.getKeyword())){
            return null;
        }
        BaseEsPageRequest baseEsPageRequest = CopyUtil.copyBean(request, BaseEsPageRequest.class);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.multiMatchQuery(request.getKeyword(),"productName"));
        return restClientHelper.highLightSearchPage(queryBuilder,baseEsPageRequest,"productName",ProductSkuDTO.class);
    }

    /**
     * @Description 构建分页
     * @author liuhu
     * @param
     * @date 2022/1/13 9:59
     * @return com.es.pojo.dto.base.BaseEsPageRequest
     */
    private  BaseEsPageRequest buildPageParam(){
        BaseEsPageRequest baseEsPageRequest = new BaseEsPageRequest();
        baseEsPageRequest.setIndexName(IndexEnum.PRODUCT.getValue());
        List<EsSortParamDTO> esSortParamDTOList = Lists.newArrayList(new EsSortParamDTO("createTime", SortOrder.DESC));
        baseEsPageRequest.setEsSortParamList(esSortParamDTOList);
        return baseEsPageRequest;
    }

    private BoolQueryBuilder buildQuery(ProductSkuDTO productSkuDTO){
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 分词查询  对于输入的搜索关键字也会分词
        if(StringUtils.isNotBlank(productSkuDTO.getProductName())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("productName",productSkuDTO.getProductName()));
        }
        // 模糊查询 类似mysql like
        if(StringUtils.isNotBlank(productSkuDTO.getBrandName())){
            boolQueryBuilder.must(QueryBuilders.wildcardQuery("brandName.keyword",productSkuDTO.getProductName()));
        }
        // = 值查询
        if(StringUtils.isNotBlank(productSkuDTO.getSkuCode())){
            boolQueryBuilder.must(QueryBuilders.termQuery("skuCode",productSkuDTO.getSkuCode()));
        }
        // in 查询  支持两个都是List 取交集
        if(!CollectionUtils.isEmpty(productSkuDTO.getCategoryCode())){
            boolQueryBuilder.must(QueryBuilders.termsQuery("categoryCode",productSkuDTO.getCategoryCode()));
        }
        //范围查询
        boolQueryBuilder.must(QueryBuilders.rangeQuery("stock").gt(0));
        if(Objects.nonNull(productSkuDTO.getCreateTimeStart()) || Objects.nonNull(productSkuDTO.getCreateTimeEnd())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("createTime");
            // 转时间戳查询
            if(Objects.nonNull(productSkuDTO.getCreateTimeStart())){
                rangeQuery.gt(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
            if(Objects.nonNull(productSkuDTO.getCreateTimeEnd())){
                rangeQuery.lt(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
            }
            boolQueryBuilder.must(rangeQuery);
        }
        // should查询
        if(Objects.nonNull(productSkuDTO.getPrice())){
            boolQueryBuilder.should(QueryBuilders.termQuery("price",productSkuDTO.getPrice()));
        }
        return boolQueryBuilder;
    }
}
