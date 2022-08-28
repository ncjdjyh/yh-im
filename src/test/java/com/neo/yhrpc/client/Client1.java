package com.neo.yhrpc.client;

import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
public class Client1 {
    public static void main(String[] args) {
        IMClient client1 = new IMClient("127.0.0.1", 8000, 1L);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("请输入消息..");
            String message = scanner.nextLine();
            System.out.println(message);
            client1.sendMessage(message, 2L);
        }
    }
}