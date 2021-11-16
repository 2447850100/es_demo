package com.example.es_demo.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.es_demo.bean.JdVo;
import com.example.es_demo.bean.User;
import com.example.es_demo.utils.ParseHtml;
import lombok.SneakyThrows;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author 小胡哥哥
 * NO BB show your code
 */
@Service
public class EsServiceImpl implements EsService{
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Override
    public User getDocument(RestHighLevelClient client, String type, Integer id) {
        GetRequest getRequest = new GetRequest(type,id+"");
        try {
             client.get(getRequest, RequestOptions.DEFAULT);
            GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
            Map<String, DocumentField> fields = response.getFields();
            System.out.println("fields = " + fields);
            String asString = response.getSourceAsString();
            User object = JSONUtil.toBean(JSONUtil.toJsonStr(asString), User.class);
            System.out.println(response);
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }

    @SneakyThrows
    @Override
    public List<JdVo> addKeyWord(String keyWord) {
        BulkRequest bulkRequest = new BulkRequest();
        List<JdVo> list = ParseHtml.getParseHtml(keyWord, JdVo.class);
        list.forEach(jdVo -> {
            bulkRequest.add(new IndexRequest("jd").source(JSONUtil.toJsonStr(jdVo), XContentType.JSON));
        });
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return list;
    }
}
