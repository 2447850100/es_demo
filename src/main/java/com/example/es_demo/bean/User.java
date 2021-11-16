package com.example.es_demo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author 小胡哥哥
 * NO BB show your code
 */
@Data
@AllArgsConstructor
public class User {
    private String userId;
    private  String userName;
    private  String password;
}
