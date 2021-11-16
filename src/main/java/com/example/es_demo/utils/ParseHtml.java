package com.example.es_demo.utils;

import cn.hutool.json.JSONUtil;
import com.example.es_demo.bean.AbstrBean;
import com.example.es_demo.bean.JdVo;
import lombok.SneakyThrows;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 小胡哥哥
 * NO BB show your code
 */

public class ParseHtml {

   @SneakyThrows
   public static List<JdVo> getParseHtml(String keyword, Class<JdVo> clazz)  {
       List<JdVo> list = new ArrayList<>();
      String url = "https://search.jd.com/Search?keyword=" + keyword;
      Document document = Jsoup.parse(new URL(url), 30000);
      Element element = document.getElementById("J_goodsList");
      Elements elements = element.getElementsByTag("li");
      for (Element el: elements) {
          JdVo abstrBean = clazz.newInstance();
          String src = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
          abstrBean.setSrc(src);
         String price = el.getElementsByClass("p-price").eq(0).text();
          abstrBean.setPrice(price);
         String name = el.getElementsByClass("p-name").eq(0).text();
         String commit = el.getElementsByTag("a").eq(0).text();
          abstrBean.setComment(commit);
          abstrBean.setName(name);
         list.add(abstrBean);
      }
      return list;
   }
}
