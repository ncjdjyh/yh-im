package com.neo.im.client;

import java.util.*;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
public class Client1 {
    public static void main(String[] args) {
        ChatClient client1 = new ChatClient("127.0.0.1", 8000, 1L);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("请输入消息..");
            String message = scanner.nextLine();
            System.out.println(message);
            client1.sendMessage(message, 2L);
        }
    }
}