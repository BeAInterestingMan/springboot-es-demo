package com.es.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.es.enums.IndexEnum;
import com.es.pojo.dto.AggSearchBaseDTO;
import com.es.pojo.dto.base.BaseEsPageRequest;
import com.es.pojo.dto.base.Page;
import com.es.pojo.dto.response.ProductMindSearchRes;
import com.es.pojo.po.ProductSku;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RestClientHelper {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * @Description ????????????
     * @author liuhu
     * @param queryBuilder
     * @param request
     * @param target
     * @date 2022/1/13 9:34
     * @return com.es.pojo.dto.base.Page<java.util.List<T>>
     */
    public <T> Page<List<T>> queryPage(BoolQueryBuilder queryBuilder, BaseEsPageRequest request, Class<T> target){
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        // es ???0??????
        searchSourceBuilder.from((request.getCurrentPage() - 1) * request.getPageSize()).size(request.getPageSize());
        SearchRequest searchRequest = new SearchRequest(request.getIndexName());
        searchRequest.source(searchSourceBuilder);
        return queryCommonPageData(searchRequest,request,target);
    }

    /**
     * @Description ????????????????????????
     * @author liuhu
     * @param searchRequest
     * @param request
     * @param target
     * @date 2022/1/14 13:43
     * @return com.es.pojo.dto.base.Page<java.util.List<T>>
     */
    private  <T> Page<List<T>>  queryCommonPageData(SearchRequest searchRequest, BaseEsPageRequest request, Class<T> target){
        Page page = new Page(request.getCurrentPage(), request.getPageSize(), 0L, new ArrayList<>());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            long total = response.getHits().getTotalHits().value;
            List<T> data = Arrays.stream(hits).map(v -> JSON.parseObject(v.getSourceAsString(), target)).collect(Collectors.toList());
            return  new Page(request.getCurrentPage(),request.getPageSize(),total,data);
        } catch (IOException e) {
            log.error("??????es????????????",e);
        }
        return  page;
    }

    /**
     * @Description ????????????
     * @author liuhu
     * @param queryBuilder ??????????????????
     * @param aggregationBuilder ????????????
     * @param aggName ?????????????????????
     * @date 2022/1/12 15:09
     * @return java.util.List<com.es.pojo.dto.AggSearchBaseDTO>
     */
    public List<AggSearchBaseDTO> aggSearch(BoolQueryBuilder queryBuilder,AggregationBuilder aggregationBuilder,String aggName){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = buildAggSearchRequest(queryBuilder, aggregationBuilder);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            ParsedStringTerms parsedStringTerms = aggregations.get(aggName);
            List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
           return  buckets.stream().map(v->{
                AggSearchBaseDTO aggSearchBaseDTO = new AggSearchBaseDTO();
                aggSearchBaseDTO.setKey(v.getKeyAsString());
                aggSearchBaseDTO.setCount(v.getDocCount());
                return aggSearchBaseDTO;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("execute aggSearch error,builder:{},aggregationBuilder:{}",queryBuilder.toString(),aggregationBuilder.toString());
            return Lists.newArrayList();
        }
    }

    /**
     * @Description ??????????????????SearchSourceBuilder
     * @author liuhu
     * @param queryBuilder  ????????????
     * @param aggregationBuilder ????????????
     * @date 2022/1/12 14:55
     * @return org.elasticsearch.search.builder.SearchSourceBuilder
     */
    private SearchSourceBuilder buildAggSearchRequest(BoolQueryBuilder queryBuilder,AggregationBuilder aggregationBuilder){
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder)
                           .aggregation(aggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        return searchSourceBuilder;
    }


    /**
     * @Description ??????????????????
     * @author liuhu
     * @param column ????????????
     * @param prefix ????????????
     * @date 2022/1/10 21:06
     * @return java.util.List<com.es.pojo.dto.response.ProductMindSearchRes>
     */
    public List<ProductMindSearchRes> queryMindSearch(String column,String prefix,String suggestGroupName) {
        log.info("execute queryMindSearch error column:{},prefix:{}",column,prefix);
        SearchRequest searchRequest = new SearchRequest(IndexEnum.PRODUCT.getValue());
        SearchSourceBuilder sourceBuilder = buildSuggestRequest(column, prefix, 10, suggestGroupName);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Suggest suggest = search.getSuggest();
            // ?????????????????????
            CompletionSuggestion completionSuggestion = suggest.getSuggestion(suggestGroupName);
            // ???????????????
            List<CompletionSuggestion.Entry.Option> options = completionSuggestion.getOptions();
            if(CollectionUtils.isEmpty(options)){
              return Lists.newArrayList();
            }
            // ????????????????????????key??????????????????value???map
            List<ProductMindSearchRes> searchRes = options.stream().map(this::buildResult).collect(Collectors.toList());
            log.info("execute queryMindSearch error column:{},prefix:{},res:{}",column,prefix,JSON.toJSONString(searchRes));
            return searchRes;
        } catch (IOException e) {
            log.error("execute aggSearch error,sourceBuilder:{}",sourceBuilder.toString(),e);
            return Lists.newArrayList();
        }
    }

    /**
     * @Description ??????????????????request
     * @author liuhu
     * @param column ??????????????????
     * @param prefix ????????????
     * @param size ????????????
     * @param suggestGroupName ????????????????????????????????????
     * @date 2022/1/12 15:13
     * @return org.elasticsearch.search.builder.SearchSourceBuilder
     */
   private SearchSourceBuilder buildSuggestRequest(String column,String prefix,Integer size,String suggestGroupName){
       SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
       SuggestBuilder suggestBuilder = new SuggestBuilder();
       // ?????????????????????prefix????????????column????????????   skipDuplicates ????????????
       suggestBuilder.addSuggestion(suggestGroupName,
               SuggestBuilders.completionSuggestion(column)
                       .prefix(prefix)
                       .skipDuplicates(true)
                       .size(size));
       searchSourceBuilder.suggest(suggestBuilder);
       return searchSourceBuilder;
    }


    /**
     * @Description ???????????????
     * @author liuhu
     * @param option
     * @date 2022/1/10 21:06
     * @return com.es.pojo.dto.response.ProductMindSearchRes
     */
    private ProductMindSearchRes buildResult(CompletionSuggestion.Entry.Option option) {
        if (Objects.isNull(option)) {
            return null;
        }
        // ????????????????????????
        Text text = option.getText();
        // ????????????????????????
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

    /**
     * @Description ????????????
     * @author liuhu
     * @param queryBuilder ????????????
     * @param filterFunctionBuilders ????????????
     * @param baseEsPageRequest es??????????????????
     * @param target ????????????
     * @date 2022/1/12 16:31
     * @return com.es.pojo.dto.base.Page<T>
     */
    public <T>Page<List<T>> scoreSearch(BoolQueryBuilder queryBuilder,FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders,
                            BaseEsPageRequest baseEsPageRequest,Class<T> target) {
        SearchRequest searchRequest = new SearchRequest(baseEsPageRequest.getIndexName());
        SearchSourceBuilder sourceBuilder = buildScoreRequest(queryBuilder, filterFunctionBuilders,baseEsPageRequest);
        searchRequest.source(sourceBuilder);
        return queryCommonPageData(searchRequest,baseEsPageRequest,target);
    }

    /**
     * @Description ????????????????????? score = query + functions
     *   scoreMode????????????function??????????????????????????????????????????   boostMode????????????query + functions?????????????????????  REPLACE?????????functions??????
     * @author liuhu
     * @param queryBuilder ????????????
     * @param filterFunctionBuilders ??????????????????
     * @date 2022/1/12 16:11
     * @return void
     */
    private SearchSourceBuilder buildScoreRequest(BoolQueryBuilder queryBuilder
            ,FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders,BaseEsPageRequest baseEsPageRequest) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // ??????????????????
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders
                .functionScoreQuery(queryBuilder, filterFunctionBuilders)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                .boostMode(CombineFunction.REPLACE);
        buildSortAndPage(baseEsPageRequest,sourceBuilder);
        sourceBuilder.query(functionScoreQueryBuilder);
        return sourceBuilder;
    }

    /**
     * @Description ??????????????????????????????
     * @author liuhu
     * @param baseEsPageRequest
     * @param sourceBuilder
     * @date 2022/1/12 16:21
     * @return void
     */
    private void buildSortAndPage(BaseEsPageRequest baseEsPageRequest,SearchSourceBuilder sourceBuilder){
        if(Objects.isNull(baseEsPageRequest)){
            return;
        }
        // ???????????? es???0????????????
        sourceBuilder.from((baseEsPageRequest.getCurrentPage()-1)*10)
                .size(baseEsPageRequest.getPageSize());
        // ????????????
        if(!CollectionUtils.isEmpty(baseEsPageRequest.getEsSortParamList())){
            baseEsPageRequest.getEsSortParamList().forEach(v->{
                sourceBuilder.sort(v.getColumn(),v.getSortOrder());
            });
        }
    }


    /**
     * @Description ????????????
     * @author liuhu
     * @param queryBuilder
     * @param request
     * @param highLightColumn ?????????????????????????????????
     * @param target
     * @date 2022/1/14 13:50
     * @return com.es.pojo.dto.base.Page<java.util.List<T>>
     */
    public <T> Page<List<T>> highLightSearchPage(BoolQueryBuilder queryBuilder,BaseEsPageRequest request,String highLightColumn, Class<T> target){
        SearchRequest searchRequest = new SearchRequest(request.getIndexName());
        SearchSourceBuilder sourceBuilder = buildHighLightQuery(highLightColumn, queryBuilder, request);
        searchRequest.source(sourceBuilder);
        Page page = new Page(request.getCurrentPage(), request.getPageSize(), 0L, new ArrayList<>());
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            long total = response.getHits().getTotalHits().value;
            List<T> data = Arrays.stream(hits).map(v -> JSON.parseObject(resolveHighLight(v,highLightColumn), target)).collect(Collectors.toList());
            //??????
            return new Page(request.getCurrentPage(), request.getPageSize(), total, data);
        } catch (IOException e) {
            log.error("??????es????????????",e);
            return page;
        }
    }

    /**
     * @Description ???????????????????????????????????????
     * @author liuhu
     * @param hit
     * @param highLightColumn
     * @date 2022/1/14 14:12
     * @return java.lang.String
     */
    private String resolveHighLight(SearchHit hit , String highLightColumn) {
        // ?????????
        String sourceData = hit.getSourceAsString();
        // ??????????????????
        HighlightField highlightField = hit.getHighlightFields().get(highLightColumn);
        if(Objects.isNull(highlightField)){
            return sourceData;
        }
        String highLightText = Arrays.stream(highlightField.getFragments()).map(String::valueOf).collect(Collectors.joining());
        // ???????????????????????????
        if(StringUtils.isBlank(highLightText)){
            return sourceData;
        }
        // ??????  ???????????????????????????????????????
        JSONObject jsonObject = JSONObject.parseObject(sourceData);
        jsonObject.put(highLightColumn,highLightText);
        return JSONObject.toJSONString(jsonObject);
    }


    /**
     * @Description ????????????????????????
     * @author liuhu
     * @param highLightColumn
     * @param queryBuilder
     * @date 2022/1/14 13:48
     * @return org.elasticsearch.search.builder.SearchSourceBuilder
     */
    private SearchSourceBuilder  buildHighLightQuery(String highLightColumn,BoolQueryBuilder queryBuilder,BaseEsPageRequest request){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        // ???????????????????????????
        highlightBuilder.field(highLightColumn)
                .preTags("<font color='red'>")
                .postTags("</font>")
                .requireFieldMatch(Boolean.FALSE);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from((request.getCurrentPage() - 1) * request.getPageSize()).size(request.getPageSize());
        return sourceBuilder.query(queryBuilder)
                .highlighter(highlightBuilder);
    }
}
