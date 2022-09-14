package com.neo.im.server;

import com.neo.im.common.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void sendMessage(Message message) {
        imServer.sendMessage(message);
    }
}
