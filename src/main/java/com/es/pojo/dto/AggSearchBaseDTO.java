package com.es.pojo.dto;

import lombok.Data;
/**
 *  @Description 聚合查询公共返回值
 *  @author liuhu
 *  @Date 2022/1/12 14:58
 */
@Data
public class AggSearchBaseDTO {
    /**聚合获得名称*/
    private String key;
    /**数量*/
    private Long count;
}
