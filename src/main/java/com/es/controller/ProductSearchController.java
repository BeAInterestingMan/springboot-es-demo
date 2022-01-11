//package com.es.controller;
//
//import com.es.pojo.request.ProductSearchRequest;
//import com.es.service.ProductSearchService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//public class ProductSearchController {
//
//    @Autowired
//    private ProductSearchService productSearchService;
//
//    @GetMapping
//    public void query(ProductSearchRequest request){
//        productSearchService.queryPage(request);
//    }
//
//    public static void main(String[] args) {
//        ArrayList<String> list = new ArrayList<>();
//        list.add("xxx");
//        list.add("qqq");
//        list.add("zzzzz");
//        List<String> list1 = list.stream().map(v -> {
//            if (v.equals("qqq")) {
//                return "";
//            }
//            return v;
//        }).collect(Collectors.toList());
//    }
//}
