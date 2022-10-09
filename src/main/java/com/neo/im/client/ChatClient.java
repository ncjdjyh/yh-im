package com.neo.im.client;

import com.neo.im.chat.ChatServer;
import com.neo.im.client.connect.ConnectChatTask;
import com.neo.im.client.connect.ConnectTaskHandler;
import com.neo.im.client.handler.ClientMessageCollector;
import com.neo.im.common.Constant;
import com.neo.im.common.payload.GroupMessage;
import com.neo.im.common.payload.Message;
import com.neo.im.common.tranform.MessageOutput;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
@Service
public class ChatClient implements InitializingBean {
    @Autowired
    private ClientMessageCollector messageCollector;
    @Autowired
    private ConnectTaskHandler connectTaskHandler;

    public void sendMessage(Message message) {
        MessageOutput output = new MessageOutput(Constant.MessageType.CHAT, message);
        messageCollector.send(output);
    }

    public void sendGroupMessage(GroupMessage message) {
        MessageOutput output = new MessageOutput(Constant.MessageType.GROUP_CHAT, message);
        messageCollector.send(output);
    }

    @Override
    public void afterPropertiesSet() {
        connectTaskHandler.connect();
    }
}
