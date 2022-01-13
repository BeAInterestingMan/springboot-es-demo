1.商品setting 使用ik先分词再用pinyin过滤

```json
PUT /product
{
  "settings": {
    "analysis": {
      "analyzer": {
        "productAnalyzer": {
          "tokenizer": "ik_max_word",
          "filter": "py"
        }
      },
      "filter": {
        "py": {
          "keep_separate_first_letter": "false",
          "lowercase": "true",
          "type": "pinyin",
          "limit_first_letter_length": "20",
          "keep_original": "true",
          "keep_full_pinyin": "false",
          "keep_joined_full_pinyin": "true"
        }
      }
    }
  }
}
```

2.mapping 

1.商品

```json
PUT /product/_mapping
{
  "properties": {
    "skuCode": {
      "type": "keyword"
    },
    "productName": {
      "type": "text",
      "analyzer": "productAnalyzer",
      "search_analyzer": "ik_max_word"
    },
    "price": {
      "type": "double"
    },
    "description": {
      "type": "text",
      "analyzer": "productAnalyzer",
      "search_analyzer": "ik_max_word"
    },
    "mainPicture": {
      "type": "keyword"
    },
    "categoryCode": {
      "type": "keyword"
    },
    "location": {
      "type": "geo_point"
    },
    "stock": {
      "type": "long"
    },
    "brandCode": {
      "type": "long"
    },
    "brandName": {
      "type": "text",
      "analyzer": "productAnalyzer",
      "search_analyzer": "ik_max_word"
    },
    "suggestText": {
      "type": "completion"
    },
    "status": {
      "type": "integer"
    },
    "createTime": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss||epoch_millis"
    },
    "updateTime": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss||epoch_millis"
    },
    "shelvesTime": {
      "type": "date",
      "format": "yyyy-MM-dd HH:mm:ss||epoch_millis"
    }
  }
}

// 插入文档
PUT /product/_doc/2
{
    "skuCode": "SKU215465485",
    "productName":"长裤nike" ,
    "price": 20.14,
    "description": "长裤nike很nice",
    "mainPicture": "",
    "categoryCode": ["10010","10020"],
    "location":"31.220706,32.1214",
    "stock": 100,
    "brandCode": "14525SA552",
    "brandName": "nike",
    "suggestText": ["长裤nike","nike"],
    "status": 1,
    "createTime": "2022-01-10 21:28:00" ,
    "updateTime": "2022-01-10 21:28:00" ,
    "shelvesTime": "2022-01-10 21:28:00"
}
```

