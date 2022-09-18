package com.neo.im.client;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neo.im.client.config.ClientInfo;
import com.neo.im.common.*;
import com.neo.im.common.payload.Message;
import com.neo.im.common.tranform.MessageDecoder;
import com.neo.im.common.tranform.MessageEncoder;
import com.neo.im.common.tranform.MessageOutput;
import com.neo.im.util.RequestId;
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
    private ClientInfo clientInfo = ClientInfo.getInstance();

    public ChatClient() {
        connectChatServer();
        connectPresenceServer();
        loginChatServer();
    }

    private void loginChatServer() {
        sendMessage("login", Constant.Command.LOGIN, clientInfo.getClientId(), -1L);
    }

    private void connectPresenceServer() {
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
        ChannelFuture f = bootstrap.connect(instance.getIp(), instance.getPort()).syncUninterruptibly();
        f.channel().closeFuture().addListener(future -> group.shutdownGracefully());
    }

    public void sendMessage(String content, int type, Long from, Long to) {
        sendMessage(new Message(from, to, content), type);
    }

    public void sendMessage(Message message, int type) {
        String requestId = RequestId.next();
        MessageOutput output = new MessageOutput(requestId, type, message);
        messageCollector.send(output);
    }

    private void connectChatServer() {
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
        ChannelFuture f = bootstrap.connect(instance.getIp(), instance.getPort()).syncUninterruptibly();
        f.channel().closeFuture().addListener(future -> closeChatServer());
    }

    public void closeChatServer() {
        group.shutdownGracefully();
    }
}
