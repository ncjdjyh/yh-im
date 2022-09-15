package com.neo.im.server;

import com.neo.im.common.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: ncjdjyh
 * @since: 2022/9/11
 */
@RestController
@RequestMapping("/api")
public class ApiEndpoint {
    @Autowired
    private IMServer imServer;

    @PostMapping("/sendMessage")
    public void sendMessage(@RequestBody Message message) {
        imServer.sendMessage(message);
    }
}
