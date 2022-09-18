package com.neo.im.client;

import java.util.Scanner;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
public class Client2 {
    public static void main(String[] args) {
        ChatClient client2 = new ChatClient();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入消息..");
            String message = scanner.nextLine();
            client2.sendMessage(message, 2L, 1L);
        }
    }
}
