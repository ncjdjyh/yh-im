package com.neo.im.client;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neo.im.client.config.ClientChatInfo;
import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
import com.neo.im.common.RegisterCenter;
import com.neo.im.common.exception.BizException;
import com.neo.im.common.payload.GroupMessage;
import com.neo.im.common.payload.Heartbeat;
import com.neo.im.common.payload.Message;
import com.neo.im.common.tranform.MessageDecoder;
import com.neo.im.common.tranform.MessageEncoder;
import com.neo.im.common.tranform.MessageOutput;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
public class ChatClient {
    private EventLoopGroup group;
    private ClientMessageCollector messageCollector = new ClientMessageCollector();
    private HeartbeatSender heartbeatSender = new HeartbeatSender();
    private ClientChatInfo clientChatInfo;

    public ChatClient(Long clientId) {
        clientChatInfo = ClientChatInfo.getInstance();
        clientChatInfo.setClientId(clientId);
        connectChatServer(clientChatInfo.getChatHostAddress());
        connectPresenceServer(clientChatInfo.getPresenceHostAddress());
        loginChatServer();
        loginPresenceServer();
    }

    private void loginPresenceServer() {
        Heartbeat heartbeat = new Heartbeat(clientChatInfo.getClientId(), JSONUtil.toJsonStr(clientChatInfo.getChatHostAddress()));
        MessageOutput output = new MessageOutput(Constant.MessageType.LOGIN, heartbeat);
        heartbeatSender.send(output);
    }

    private void loginChatServer() {
        Heartbeat heartbeat = new Heartbeat(clientChatInfo.getClientId(), "login");
        MessageOutput messageOutput = new MessageOutput(Constant.MessageType.LOGIN, heartbeat);
        messageCollector.send(messageOutput);
    }


    public void sendMessage(Message message) {
        MessageOutput output = new MessageOutput(Constant.MessageType.CHAT, message);
        messageCollector.send(output);
    }

    public void sendMessage(GroupMessage message) {
        MessageOutput output = new MessageOutput(Constant.MessageType.GROUP_CHAT, message);
        messageCollector.send(output);
    }

    private void connectPresenceServer(HostAddress presenceHostAddress) {
        Instance instance = RegisterCenter.getOneHealthyInstance(Constant.ServiceName.PRESENCE_SERVICE);
        if (ObjectUtil.isNull(instance)) {
            throw new BizException("no available service");
        }

        group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipe = ch.pipeline();
                // 3s 发送一次心跳
                pipe.addLast(new IdleStateHandler(0, 0, 3));
                pipe.addLast(new MessageEncoder());
                pipe.addLast(heartbeatSender);
            }

        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = bootstrap.connect(presenceHostAddress.getIp(), presenceHostAddress.getChatPort()).syncUninterruptibly();
        f.channel().closeFuture().addListener(future -> group.shutdownGracefully());
    }

    private void connectChatServer(HostAddress chatHostAddress) {
        Instance instance = RegisterCenter.getOneHealthyInstance(Constant.ServiceName.CHAT_SERVICE);
        if (ObjectUtil.isNull(instance)) {
            throw new BizException("no available service");
        }

        group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipe = ch.pipeline();
                pipe.addLast(new MessageDecoder());
                pipe.addLast(new MessageEncoder());
                pipe.addLast(messageCollector);
            }

        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = bootstrap.connect(chatHostAddress.getIp(), chatHostAddress.getChatPort()).syncUninterruptibly();
        f.channel().closeFuture().addListener(future -> closeChatServer());
    }

    public ClientChatInfo getClientChatInfo() {
        return clientChatInfo;
    }

    public void closeChatServer() {
        group.shutdownGracefully();
    }
}
