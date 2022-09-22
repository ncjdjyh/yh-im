package com.neo.im.client;

import com.neo.im.common.payload.Message;

import java.util.Scanner;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
public class Client2 {
    public static void main(String[] args) {
        ChatClient client2 = new ChatClient(2L);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入消息..");
            String content = scanner.nextLine();
            client2.sendMessage(new Message(client2.getClientChatInfo().getClientId(), 1L, content));
        }
    }
}
