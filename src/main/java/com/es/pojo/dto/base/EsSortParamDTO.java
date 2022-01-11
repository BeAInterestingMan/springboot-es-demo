package com.es.pojo.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.sort.SortOrder;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EsSortParamDTO {

    /**排序字段*/
    private String column;

    /**排序顺序*/
    private SortOrder sortOrder;
}
