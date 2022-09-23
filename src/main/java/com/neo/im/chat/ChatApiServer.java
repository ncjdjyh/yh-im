package com.neo.im.chat;

import com.neo.im.common.payload.GroupMessage;
import com.neo.im.common.payload.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ncjdjyh
 * @since 2022/9/11
 */
@RestController
@RequestMapping("/api")
public class ChatApiServer {
    @Autowired
    private ChatServer chatServer;

    @PostMapping("/sendMessage")
    public void sendMessage(@RequestBody Message message) {
        chatServer.sendMessage(message);
    }

    @PostMapping("/sendGroupMessage")
    public void sendGroupMessage(@RequestBody GroupMessage message) {
        chatServer.sendGroupMessage(message);
    }
}
