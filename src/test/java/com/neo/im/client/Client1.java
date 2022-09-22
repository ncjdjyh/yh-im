package com.neo.im.client;

import com.neo.im.common.Constant;
import com.neo.im.common.payload.Message;

import java.util.*;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
public class Client1 {
    public static void main(String[] args) {
        ChatClient client1 = new ChatClient(1L);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("请输入消息..");
            String content = scanner.nextLine();
            System.out.println(content);
            client1.sendMessage(new Message(client1.getClientChatInfo().getClientId(), 2L, content));
        }
    }
}