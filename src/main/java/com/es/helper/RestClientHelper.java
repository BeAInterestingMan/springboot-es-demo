package com.es.helper;

import com.alibaba.fastjson.JSON;
import com.es.enums.IndexEnum;
import com.es.pojo.dto.base.Page;
import com.es.pojo.dto.base.BaseEsPageRequest;
import com.es.pojo.dto.response.ProductMindSearchRes;
import com.es.pojo.po.ProductSku;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.util.CollectionUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RestClientHelper {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    private <T> Page<T> queryPage(BoolQueryBuilder queryBuilder, BaseEsPageRequest request, Class<T> target){
        Page page = new Page(request.getCurrentPage(), request.getPageSize(), 0L, new ArrayList<>());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        // es 从0开始
        searchSourceBuilder.from((request.getCurrentPage() - 1) * request.getPageSize()).size(request.getPageSize());
        SearchRequest searchRequest = new SearchRequest(request.getIndexName());
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            long total = response.getHits().getTotalHits().value;
            List<T> data = Arrays.stream(hits).map(v -> JSON.parseObject(v.getSourceAsString(), target)).collect(Collectors.toList());
           return  new Page<>(request.getCurrentPage(),request.getPageSize(),total,data);
        } catch (IOException e) {
           log.error("查询es分页异常",e);
        }
        return  page;
    }

    /**
     * @Description 商品联想搜索
     * @author liuhu
     * @param column 搜索字段
     * @param prefix 搜索内容
     * @date 2022/1/10 21:06
     * @return java.util.List<com.es.pojo.dto.response.ProductMindSearchRes>
     */
    public List<ProductMindSearchRes> queryMindSearch(String column,String prefix) {
        log.info("execute queryMindSearch error column:{},prefix:{}",column,prefix);
        SearchRequest searchRequest = new SearchRequest(IndexEnum.PRODUCT.getValue());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        // 查询内容前缀为prefix，字段名column的联想词   skipDuplicates 跳过重复
        suggestBuilder.addSuggestion("product_suggestion",
                SuggestBuilders.completionSuggestion(column)
                        .prefix(prefix)
                        .skipDuplicates(true)
                        .size(10));
        searchSourceBuilder.suggest(suggestBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Suggest suggest = search.getSuggest();
            // 获取指定的结果
            CompletionSuggestion completionSuggestion = suggest.getSuggestion("product_suggestion");
            // 获取建议项
            List<CompletionSuggestion.Entry.Option> options = completionSuggestion.getOptions();
            if(CollectionUtils.isEmpty(options)){
              return Lists.newArrayList();
            }
            // 获取命中关键词为key，全部内容为value的map
            List<ProductMindSearchRes> searchRes = options.stream().map(this::buildResult).collect(Collectors.toList());
            log.info("execute queryMindSearch error column:{},prefix:{},res:{}",column,prefix,JSON.toJSONString(searchRes));
            return searchRes;
        } catch (IOException e) {
            log.error("execute queryMindSearch error column:{},prefix:{}",column,prefix);
            return Lists.newArrayList();
        }
    }

    /**
     * @Description 构建返回值
     * @author liuhu
     * @param option
     * @date 2022/1/10 21:06
     * @return com.es.pojo.dto.response.ProductMindSearchRes
     */
    private ProductMindSearchRes buildResult(CompletionSuggestion.Entry.Option option) {
        if (Objects.isNull(option)) {
            return null;
        }
        // 获取联想推荐内容
        Text text = option.getText();
        // 获取商品索引数据
        String sourceData = option.getHit().getSourceAsString();
        if (StringUtils.isBlank(sourceData)) {
            return null;
        }
        ProductSku productSku = JSON.parseObject(sourceData, ProductSku.class);
        if (Objects.isNull(productSku)) {
            return null;
        }
        return ProductMindSearchRes.builder()
                .content(text.toString())
                .skuCode(productSku.getSkuCode()).build();
    }
}
