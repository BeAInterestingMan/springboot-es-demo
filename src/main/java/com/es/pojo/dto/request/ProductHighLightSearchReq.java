package com.es.pojo.dto.request;

import com.es.pojo.dto.base.BaseEsPageRequest;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 *  @Description 高亮搜索请求值
 *  @author liuhu
 *  @Date 2022/1/10 19:59
 */
@Data
public class ProductHighLightSearchReq extends BaseEsPageRequest {

    /**商品sku编码*/
    private String keyword;
}
