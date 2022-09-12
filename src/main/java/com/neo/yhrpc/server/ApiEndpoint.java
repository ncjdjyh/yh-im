package com.neo.yhrpc.server;

import cn.hutool.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: ncjdjyh
 * @since: 2022/9/11
 */
@RestController("/api")
public class ApiEndpoint {
    @PostMapping("/sendMessage")
    public JSONObject sendMessage() {
        return null;
    }
}
