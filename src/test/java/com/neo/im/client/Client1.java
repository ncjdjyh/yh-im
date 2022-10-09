package com.neo.im.client;

import com.neo.im.Application;
import com.neo.im.client.config.ClientChatInfo;
import com.neo.im.common.payload.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Scanner;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
public class Client1 {
    @Autowired
    ChatClient chatClient;
    @Autowired
    private ClientChatInfo clientChatInfo;

    @Test
    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        clientChatInfo.setClientId(1L);

        while (true) {
            System.out.println("请输入消息..");
            String content = scanner.nextLine();
            System.out.println(content);
            chatClient.sendMessage(new Message(clientChatInfo.getClientId(), 2L, content));
        }
    }
}