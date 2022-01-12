# ES学习

### 1.基本概念

#### 近实时

​    1.从写入数据到数据可以被搜索到有一个小延迟（大概1秒）；

​     2.基于es执行搜索和分析可以达到秒级

####  索引

包含一堆有相似结构的文档数据，比如可以有一个客户索引，商品分类索引，订单索引，索引有一个名称。一个index包含很多document，一个index就代表了一类类似的或者相同的document。比如说建立一个product index，商品索引，里面可能就存放了所有的商品数据，所有的商品document。

#### 文档和字段

文档，es中的最小数据单元，一个document可以是一条客户数据，一条商品分类数据，一条订单数据，用JSON数据结构表示，每个index下的type中，都可以去存储多个document。一个document里面有多个field，每个field就是一个数据字段

#### shard

单台机器无法存储大量数据，es可以将一个索引中的数据切分为多个shard（一个index包含多个shard ），分布在多台服务器上存储。有了shard就可以横向扩展，存储更多数据，让搜索和分析等操作分布到多台服务器上去执行，提升吞吐量和性能。每个shard都是一个最小工作单元，承载部分数据。

### 2.分词器

#### iK分词器

```java
iK分词器
  1.ik_max_word  最大化分词
 GET /_analyze
{
  "analyzer": "ik_max_word",
  "text": "我是一个中国人"
} 
结果
   {
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "一个中国",
      "start_offset" : 2,
      "end_offset" : 6,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "一个",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 3
    },
    {
      "token" : "一",
      "start_offset" : 2,
      "end_offset" : 3,
      "type" : "TYPE_CNUM",
      "position" : 4
    },
    {
      "token" : "个中",
      "start_offset" : 3,
      "end_offset" : 5,
      "type" : "CN_WORD",
      "position" : 5
    },
    {
      "token" : "个",
      "start_offset" : 3,
      "end_offset" : 4,
      "type" : "COUNT",
      "position" : 6
    },
    {
      "token" : "中国人",
      "start_offset" : 4,
      "end_offset" : 7,
      "type" : "CN_WORD",
      "position" : 7
    },
    {
      "token" : "中国",
      "start_offset" : 4,
      "end_offset" : 6,
      "type" : "CN_WORD",
      "position" : 8
    },
    {
      "token" : "国人",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "CN_WORD",
      "position" : 9
    }
  ]
}
  2.ik_smart     智能化分词 
  分析
      GET /_analyze
    {
      "analyzer": "ik_smart",
      "text": "我是一个中国人"
    } 
结果
   {
  "tokens" : [
    {
      "token" : "我",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "CN_CHAR",
      "position" : 0
    },
    {
      "token" : "是",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "一个",
      "start_offset" : 2,
      "end_offset" : 4,
      "type" : "CN_WORD",
      "position" : 2
    },
    {
      "token" : "中国人",
      "start_offset" : 4,
      "end_offset" : 7,
      "type" : "CN_WORD",
      "position" : 3
    }
  ]
} 
      
```

#### 拼音分词器

```json
分析
GET /_analyze
{
  "analyzer": "pinyin",
  "text": "我是一个中国人"
}
结果
{
  "tokens" : [
    {
      "token" : "wo",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "wsygzgr",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "shi",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 1
    },
    {
      "token" : "yi",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 2
    },
    {
      "token" : "ge",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 3
    },
    {
      "token" : "zhong",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 4
    },
    {
      "token" : "guo",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 5
    },
    {
      "token" : "ren",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 6
    }
  ]
}

```

#### 自定义分词器

```
1.自定义分词器，即使用ik又使用pinyin

正常的拼音分词器，会把一个字整一个拼音再加一个全拼，类似
{
  "tokens" : [
    {
      "token" : "wo",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "wsygzgr",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "shi",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 1
    },
    {
      "token" : "yi",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 2
    },
    {
      "token" : "ge",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 3
    },
    {
      "token" : "zhong",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 4
    },
    {
      "token" : "guo",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 5
    },
    {
      "token" : "ren",
      "start_offset" : 0,
      "end_offset" : 0,
      "type" : "word",
      "position" : 6
    }
  ]
}
如果我需要中国人zgr拼音搜索，则达不到要求,所以需要ik+pinyin
```

```
分词器由三部分构成
1.character filters:在tokenizer之前对文本做处理，例如删除字符或者替换字符
2.tokenizer：将文本按照一定的规则把文本切割成词条。
3.tokenizer filter:对分词得到的结果再做一定的处理。例如拼音处理，大小写转换

先用ik做分词处理，再用pinyin做过滤处理，注意自定义分词必须要在索引创建之前处理，settings,且只对于当前索引库有效
{
  "settings": {
    "analysis": {
      "analyzer": {
        "myAnalyzer":{
          "tokenizer":"ik_max_word",
          "filter":"py"
        }
      },
      "filter": {
        "py":{
              "keep_separate_first_letter" : "false",
              "lowercase" : "true",
              "type" : "pinyin",
              "limit_first_letter_length" : "20",
              "keep_original" : "true",
              "keep_full_pinyin" : "true",
              "keep_joined_full_pinyin" : "true"
        }
      }
    }
  }
再次搜索结果为  
GET /person/_analyze
{
  "analyzer": "myAnalyzer",
  "text": "我是一个中国人"
}
一部分结果明显看出pinying也进行了分词。
{
    "token" : "一个中国",
    "start_offset" : 2,
    "end_offset" : 6,
    "type" : "CN_WORD",
    "position" : 5
},
{
    "token" : "yigezhongguo",
    "start_offset" : 2,
    "end_offset" : 6,
    "type" : "CN_WORD",
    "position" : 5
},
{
    "token" : "ygzg",
    "start_offset" : 2,
    "end_offset" : 6,
    "type" : "CN_WORD",
    "position" : 5
}

注意：拼音分词器适合在创建倒排索引的时候使用，也就是对于分档内容进行拼音分词，不适用进行搜索，因为会对搜索输入的关键字进行分词，搜索大米，能把所有文档含有dm的文档搜索出来，会带出同音字。
1.用户输的是拼音才进行拼音搜索
2.用户输入中文搜索的也应该是中文，不能pinyin搜索
解决办法：设置创建倒排索引时和搜索时用不一样的分词器
"anaimal":{
        "type": "text",
        "analyzer": "myAnalyzer",
        "search_analyzer": "ik_max_word"
      },
```



### 3.mapping属性

#### type

```java

1.字符串类型
  a.text  可用来分词
  b.keyword  不可用来分词 （精确值）
  
2.数值类型  
  long integer short byte double float
  
3.布尔
  boolean

4.日期
  date

5.对象
  object
  
6.geo_point 地理位置坐标  常见常见附件的人，我的位置    
    
注意:es中没有数组类型，任意一个类型都可以存储数组数据。
  
```

#### index

```
是否创建索引，默认为true,代表该字段参与搜索
```

#### analyzer

具体使用哪种分词器

#### properties

字段的子字段，常见表现为对象套对象



#### demo

```json
PUT /person
{
  "mappings": {
    "properties": {
      "id":{
        "type": "long"
      },
      "age":{
        "type": "integer"
      },
       "sex":{
        "type": "boolean"
      },
       "desc":{
        "type": "text",
        "analyzer": "ik_max_word"
      },
       "place":{
        "type": "keyword"
      },
      "location":{
        "type": "geo_point"
      },
      "name":{
        "type":"object", 
        "properties": {
          "firstName":{
            "type":"text",
            "analyzer": "ik_max_word"
          },
          "sencondName":{
            "type":"text",
            "analyzer": "ik_max_word"
          }
        }
      }
    }
  }
}
```





### 4.DSL

####  索引相关

```
1.查看索引
GET /person/_mapping
2.创建索引
PUT /indexName
3.删除索引
DELETE /indexName
4.修改索引
PUT /indexName/_mapping
{
"properties":{
  }
}

注意:es禁止修改索引库mapping的类型，因为底层倒排索引创建好了，修改类型会导致一系列问题，可以新增索引类型
```

文档

```
1.创建文档
PUT /person/_doc/1
{
  "id": 1,
  "age": 12,
  "sex": true,
  "desc": "小明是一个爱生气的人",
  "place": "上海浦东",
  "name": {
    "firstName": "小明",
    "sencondName": "明明"
  },
  "location":"31.220706,32.1214"
}
2.查看文档
GET /person/_doc/1

3.删除文档
DELETE /person/_doc/1

4.修改文档
 1.全量修改数据
   
 2.脚本修改
    POST /person/_update_by_query
    {
      "script": {
        "inline": "ctx._source.place='安徽合肥'",
         "lang": "painless"
      },
      "query": {
        "term": {
          "id": "1"
        }
      }
    }
 
```

#### 常用查询

```json
1.查询所有 match_all
    GET /person/_search
        {
        "query": {
        "match_all": {}
        }
    }

2.全文检索（利用分词器对用户输入的内容进行分词，然后倒排索引库匹配）
  1.match: 
    GET /person/_search
    {
      "query": {
        "match": {
          "desc": "幽默的啊啊啊啊"
        }
      }
    }
    
  2.multi_match: 可以多个字段分词匹配
  
   GET /person/_search
    {
      "query": {
        "multi_match": {
          "fields": [
              "name.firstName",
              "desc"
            ],
            "query": "幽默小明"
        }
      }
    }
    
3.精准查询
  1.term  不会对用户输入的进行分词，精准查询

  GET /person/_search
{
  "query": {
    "term": {
      "place": {
        "value": "上海浦东"
      }
    }
  }
}

2.range 范围查询
  GET /person/_search
{
  "query": {
     "range": {
       "age": {
         "gte": 0,
         "lte": 20
       }
     }
  }
}

5.地理位置查询
 geo_bounding_box:查询某个矩形范围内的坐标的文档
  
 geo_distance: 查询到指定中心点小于某个距离值的文档
 GET /person/_search
    {
      "query": {
        "geo_distance":{
          "distance":"15km",
          "location":"31.3214,32.5214"
        }
      }
    } 
    
6.复合查询
1.bool查询 一个或者多个查询子句的组合

    must等于，内部and查询   
    must_not一定不等于，内部and链接  
    should 内部or连接
    filter 和 must类似，只是不参与评分

GET /person/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "term": {
            "FIELD": {
              "value": "VALUE"
            }
          },
          "range": {
            "FIELD": {
              "gte": 10,
              "lte": 20
            }
          }
        }
      ],
      "must_not": [
        {
          "term": {
            "FIELD": {
              "value": "VALUE"
            }
          }
        }
      ],
      "should": [
        {
          
        }
      ]
    }
  }
}
```

#### 评分查询

```
评分查询：控制命中指定关键词的排名  TF-IDF算法
 常见场景:节假日是茅台品牌的酒类商品优先展示
 query 表示原本的查询条件(会根据文档的相关性算分)，filter表示评分的条件那些文档需要加分  weight表示常量分  值，即命中关键字茅台的文档分为5分
 一个文档的最终分值 = query + function +运算函数
 
 GET /person/_search
{
  "query": {
    "function_score": {
      "query": {
        "term": {
          "FIELD": {
            "value": "VALUE"
          }
        }
      },
      "functions": [
        {
          "filter": {
            "term": {
              "desc": "茅台"
            }
          },
          "weight": 5
        }
      ]
    }
  }
}
boost_mode 加权模式：定义了queryScore和functionSocre的运算方式，包括
multiply:两者相乘
replace:用functionSocre 替代 queryScore
max: 取两者最大值
min: 取两者最小值
sum:相加

score_mode 定义的是如何将各个function的分值合并成一个综合的分值； 
boost_mode 则定义如何将这个综合的分值作用在原始query产生的分值上

注意：es中同样的一条数据，因为在不同的shard分值可能不同
```



#### 高亮搜索

```
在搜索结果中把搜索关键字标记出来
GET /person/_search
{
  "query": {
    "term": {
      "desc": {
        "value": "小明"
      }
    }
  },
  "highlight": {
    "fields": {
      "desc": {
        "pre_tags": "<em>",
        "post_tags": "</em>"
      }
    }
  }
}
```

#### 数据聚合

```
agg数据聚合查询  把相同属性的文档放入到同一个bucket中
GET /person/_search
{
  "aggs": {
    "ageGroup": {
      "terms": {
				"field": "age"
			}
    },
    "placeGroup":{
      "terms": {
				"field": "place"
			}
    }
  }
}
类似mysql groupby的功能
```

### 5.搜索补全

```
ES中completion类型可以用来做搜索补全
"suggest":{
        "type": "completion"
 },
 
GET /person/_search
{
  "suggest": {
    "my_suggest": {
      "text": "中",
      "completion":{
       "field":"suggest",
        "size":10
      }
    }
  }
}
取中字开头的前10个建议
```

