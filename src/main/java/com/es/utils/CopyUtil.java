package com.es.utils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *  @Description copy工具类
 *  @author liuhu
 *  @Date 2022/1/12 15:38
 */
@Slf4j
public class CopyUtil {

    public static <S,T> List<T> copyList(List<S> sourceList,Class<T> target){
        List<T> list = new ArrayList<>();
        if(Objects.isNull(target)){
            return Lists.newArrayList();
        }
        if(CollectionUtils.isEmpty(sourceList)){
            return Lists.newArrayList();
        }
        try {
            sourceList.forEach(s->{
                T t = copyBean(s,target);
                list.add(t);
            });
        } catch (Exception e) {
            log.error("List类型copy失败",e);
        }
        return list;
    }


    public static <T> T copyBean(Object source,Class<T> target){
        if(Objects.isNull(target) || Objects.isNull(source)){
            return null;
        }
        try {
            T t = target.newInstance();
            BeanUtils.copyProperties(source,t);
            return t;
        } catch (Exception e) {
            log.error("类型转换失败",e);
            return null;
        }
    }
}
