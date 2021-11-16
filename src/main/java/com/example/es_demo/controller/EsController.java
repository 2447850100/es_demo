package com.example.es_demo.controller;

import com.example.es_demo.bean.User;
import com.example.es_demo.service.EsService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 小胡哥哥
 * NO BB show your code
 */
@RestController
@RequestMapping("/es")
public class EsController {
    @Autowired
    private EsService esService;
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;
    @RequestMapping("/query/{type}/{id}")
    public void query(@PathVariable String type,@PathVariable("id") Integer id) {
       User user =  esService.getDocument(client,type,id);
        System.out.println("user = " + user);
    }

    @GetMapping("insert/{keyWord}")
    public Object insert(@PathVariable("keyWord") String keyWord) {
         return esService.addKeyWord(keyWord);
    }
}
