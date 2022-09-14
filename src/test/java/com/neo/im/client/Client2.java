package com.neo.im.client;

import java.util.Scanner;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
public class Client2 {
    public static void main(String[] args) {
        IMClient client2 = new IMClient("127.0.0.1", 8000, 2L);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入消息..");
            String message = scanner.nextLine();
            client2.sendMessage(message, 1L);
        }
    }
}
