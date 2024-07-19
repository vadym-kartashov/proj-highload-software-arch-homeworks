# hla11

The idea of this search approach is to split search query into two terms:
1) Root of the query - first 7 letters. Fuzzy search with 2 edit distance applied here 
2) Query suffix - the rest of the query. Fuzzy search with 1 edit distance applied here

It does not cover the case when there are more than typos done in the suffix or more than 3 typos in the root, but in general 
the such cases are not common.
Another con of this approach is a necessity to break down words during indexing. 

## Query/Response examples

### 1 Typo
Query to find word ```pregnancy```
```
{
  "query": {
    "bool": {
      "must": [
        {
          "fuzzy": {
            "root": {
              "value": "gregnan",
              "fuzziness": "2"
            }
          }
        },
        {
          "fuzzy": {
            "suffix": {
              "value": "cy",
              "fuzziness": "1"
            }
          }
        }
      ]
    }
  }
}
```
Response:
```
{
    "took": 21,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 3,
            "relation": "eq"
        },
        "max_score": 13.794386,
        "hits": [
            {
                "_index": "autocomplete",
                "_id": "0RG80IkBrlxZoxdK_XDc",
                "_score": 13.794386,
                "_source": {
                    "title": "pregnancy",
                    "length": 9,
                    "root": "pregnan",
                    "suffix": "cy"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "0BG80IkBrlxZoxdK_XDc",
                "_score": 11.589939,
                "_source": {
                    "title": "pregnance",
                    "length": 9,
                    "root": "pregnan",
                    "suffix": "ce"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "LxG80IkBrlxZoxdK_bfi",
                "_score": 7.821245,
                "_source": {
                    "title": "regnancy",
                    "length": 8,
                    "root": "regnanc",
                    "suffix": "y"
                }
            }
        ]
    }
}
```
### 2 Typos
Query to find word ```pregnancy```
```
{
  "query": {
    "bool": {
      "must": [
        {
          "fuzzy": {
            "root": {
              "value": "gragnan",
              "fuzziness": "2"
            }
          }
        },
        {
          "fuzzy": {
            "suffix": {
              "value": "cy",
              "fuzziness": "1"
            }
          }
        }
      ]
    }
  }
}
```
Response:
```
{
    "took": 25,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 6,
            "relation": "eq"
        },
        "max_score": 12.127924,
        "hits": [
            {
                "_index": "autocomplete",
                "_id": "AQ-80IkBrlxZoxdK_Xeo",
                "_score": 12.127924,
                "_source": {
                    "title": "fragrancy",
                    "length": 9,
                    "root": "fragran",
                    "suffix": "cy"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "0RG80IkBrlxZoxdK_XDc",
                "_score": 12.127924,
                "_source": {
                    "title": "pregnancy",
                    "length": 9,
                    "root": "pregnan",
                    "suffix": "cy"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "RRK80IkBrlxZoxdK_Ubt",
                "_score": 12.127924,
                "_source": {
                    "title": "stagnancy",
                    "length": 9,
                    "root": "stagnan",
                    "suffix": "cy"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "_w-80IkBrlxZoxdK_Xao",
                "_score": 9.923477,
                "_source": {
                    "title": "fragrance",
                    "length": 9,
                    "root": "fragran",
                    "suffix": "ce"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "0BG80IkBrlxZoxdK_XDc",
                "_score": 9.923477,
                "_source": {
                    "title": "pregnance",
                    "length": 9,
                    "root": "pregnan",
                    "suffix": "ce"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "RBK80IkBrlxZoxdK_Ubt",
                "_score": 9.923477,
                "_source": {
                    "title": "stagnance",
                    "length": 9,
                    "root": "stagnan",
                    "suffix": "ce"
                }
            }
        ]
    }
}
```
### 3 Typos
Query to find word ```pregnancy```
```
{
  "query": {
    "bool": {
      "must": [
        {
          "fuzzy": {
            "root": {
              "value": "gragnan",
              "fuzziness": "2"
            }
          }
        },
        {
          "fuzzy": {
            "suffix": {
              "value": "sy",
              "fuzziness": "1"
            }
          }
        }
      ]
    }
  }
}
```
Response:
```
{
    "took": 22,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 3,
            "relation": "eq"
        },
        "max_score": 10.312756,
        "hits": [
            {
                "_index": "autocomplete",
                "_id": "AQ-80IkBrlxZoxdK_Xeo",
                "_score": 10.312756,
                "_source": {
                    "title": "fragrancy",
                    "length": 9,
                    "root": "fragran",
                    "suffix": "cy"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "0RG80IkBrlxZoxdK_XDc",
                "_score": 10.312756,
                "_source": {
                    "title": "pregnancy",
                    "length": 9,
                    "root": "pregnan",
                    "suffix": "cy"
                }
            },
            {
                "_index": "autocomplete",
                "_id": "RRK80IkBrlxZoxdK_Ubt",
                "_score": 10.312756,
                "_source": {
                    "title": "stagnancy",
                    "length": 9,
                    "root": "stagnan",
                    "suffix": "cy"
                }
            }
        ]
    }
}
```