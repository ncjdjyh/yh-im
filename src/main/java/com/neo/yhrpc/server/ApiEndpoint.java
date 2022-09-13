package com.neo.yhrpc.server;

import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ncjdjyh
 * @since: 2022/9/11
 */
@RestController("/api")
public class ApiEndpoint {
    @Autowired
    private IMServer imServer;

    @PostMapping("/sendMessage")
    public JSONObject sendMessage() {
        imServer
    }

    @Bean
    IMServer imServer() {
        return new IMServer("127.0.0.1", 8000);
    }
}
