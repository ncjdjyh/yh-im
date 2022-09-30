package com.neo.im.client;

import com.neo.im.common.payload.GroupMessage;

import java.util.Scanner;

/**
 * @author ncjdjyh
 * @since 2022/9/23
 */
public class GroupClient3 {
    public static void main(String[] args) {
        ChatClient client1 = new ChatClient(3L);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("请输入消息..");
            String content = scanner.nextLine();
            System.out.println(content);
            client1.sendGroupMessage(new GroupMessage(client1.getClientChatInfo().getClientId(),content, 1L));
        }
    }
}
