package com.example.es_demo.service;

import com.example.es_demo.bean.JdVo;
import com.example.es_demo.bean.User;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;

/**
 * @author 小胡哥哥
 * NO BB show your code
 */
public interface EsService {

    User getDocument(RestHighLevelClient client, String type, Integer id);

    List<JdVo> addKeyWord(String keyWord);
}
