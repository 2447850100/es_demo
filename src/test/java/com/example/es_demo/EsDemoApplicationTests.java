package com.example.es_demo;

import cn.hutool.json.JSONUtil;
import com.example.es_demo.bean.JdVo;
import com.example.es_demo.bean.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class EsDemoApplicationTests {
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

   @Test
    public void creatIndex() throws IOException {
       CreateIndexRequest request = new CreateIndexRequest("jd");
       CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

       System.out.println("createIndexResponse = " + createIndexResponse);
   }
   @Test
    public void getIndex() throws IOException {
       GetIndexRequest getIndexRequest = new GetIndexRequest("xiaohu_index1");
       boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
       System.out.println("exists = " + exists);
   }
    @Test
    public void queryIndex() throws IOException {
        DeleteIndexRequest getIndexRequest = new DeleteIndexRequest("xiaohu_index");
        AcknowledgedResponse delete = client.indices().delete(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println("delete = " + delete);
    }
    //增加文档
    @Test
    public void addDocument() throws IOException {
       IndexRequest request = new IndexRequest("user");
       request.id("1");
       request.timeout(TimeValue.timeValueSeconds(1));
        User user = new User("1", "xiaohu", "23");
        request.source(JSONUtil.toJsonStr(user), XContentType.JSON);
        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
        System.out.println("index = " + index.getResult());
        System.out.println("index = " + index.status());

    }
    @Test
    public void deleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("user","99");
        request.timeout(TimeValue.timeValueSeconds(1));
        DeleteResponse delete = client.delete(request, RequestOptions.DEFAULT);
        System.out.println("index = " + delete.getResult());
        System.out.println("index = " + delete.status());

    }
    @Test
    public void BulkDocument() throws Exception {
        List<User> users = new LinkedList<>();
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("20s");
        for (int i = 0; i < 100; i++) {
            User user = new User("" + (i + 1), "xiaohu" + i, String.valueOf(20 + i));
            users.add(user);
        }

        int[] num = {0};
        users.forEach(user -> {
            //批量插入
            // bulkRequest.add(new IndexRequest("user").id(String.valueOf(num[0]++)).source(JSONUtil.toJsonStr(user),XContentType.JSON));
            //批量查询
            GetRequest request = new GetRequest("user", String.valueOf(num[0]++));
            try {
                GetResponse response = client.get(request, RequestOptions.DEFAULT);
                System.out.println("responses = " + response );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
    @Test
    public void search() throws Exception {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("userName", "xiaohu_42");
        MatchAllQueryBuilder matchQueryBuilder = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(matchQueryBuilder);
        searchSourceBuilder.from(1);
        searchSourceBuilder.size(10);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("response = " + response);
    }
    @Test
    public void insertJD() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        String url = "https://search.jd.com/Search?keyword=java";
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");
        System.out.println("elements = " + elements.size());
        int count = 0;
        bulkRequest.timeout("20s");
        for (Element el: elements) {
            JdVo jdVo = new JdVo();
            String src = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            jdVo.setSrc(src);
            String price = el.getElementsByClass("p-price").eq(0).text();
            jdVo.setPrice(price);
            String name = el.getElementsByClass("p-name").eq(0).text();
            String commit = el.getElementsByTag("a").eq(0).text();
            jdVo.setComment(commit);
            jdVo.setName(name);

            bulkRequest.add(new IndexRequest("jd").id(String.valueOf(count++)).source(JSONUtil.toJsonStr(jdVo),XContentType.JSON));
        }
        BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("responses = " + responses);
    }
}
