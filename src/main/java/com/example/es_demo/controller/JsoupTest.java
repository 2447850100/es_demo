package com.example.es_demo.controller;

import com.example.es_demo.bean.JdVo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author 小胡哥哥
 * NO BB show your code
 */
public class JsoupTest {
    public static void main(String[] args) throws IOException {
        String url = "https://search.jd.com/Search?keyword=java";
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
        Elements elements = element.getElementsByTag("li");
        for (Element el: elements) {
            JdVo jdVo = new JdVo();
            String src = el.getElementsByTag("img").eq(0).attr("src");
            jdVo.setSrc(src);
            String price = el.getElementsByClass("p-price").eq(0).text();
            jdVo.setPrice(price);
            String name = el.getElementsByClass("p-name").eq(0).text();
            jdVo.setName(name);
            System.out.println("name = " + jdVo);
        }
    }

}
