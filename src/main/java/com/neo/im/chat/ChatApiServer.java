package com.neo.im.chat;

import com.neo.im.common.payload.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
