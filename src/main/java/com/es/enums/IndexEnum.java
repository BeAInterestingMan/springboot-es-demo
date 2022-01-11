package com.es.enums;
/**
 *  @Description es索引枚举
 *  @author liuhu
 *  @Date 2022/1/10 20:20
 */
public enum IndexEnum {

    PRODUCT("商品索引","product")

    ;
    private String name;

    private String value;

    IndexEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
