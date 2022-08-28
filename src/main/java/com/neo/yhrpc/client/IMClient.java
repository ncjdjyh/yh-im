package com.neo.yhrpc.client;

import com.neo.yhrpc.common.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
public class IMClient {
    private String ip;
    private int port;
    private EventLoopGroup group;
    private ClientMessageCollector collector;
    private Bootstrap bootstrap;
    private Long clientId;

    public IMClient(String host, int port, Long clientId) {
        this.ip = host;
        this.port = port;
        this.clientId = clientId;
        init();
        login();
    }

    public void sendMessage(String message, String type, Long from, Long to) {
        sendMessage(new Message(from, to, message), type);
    }

    public void sendMessage(Message message, String type) {
        String requestId = RequestId.next();
        MessageOutput output = new MessageOutput(requestId, type, message);
        collector.send(output);
    }

    public void login() {
        sendMessage("", Constant.Command.LOGIN, this.clientId, null);
    }

    public void sendMessage(String message, Long to) {
        sendMessage(message, Constant.Command.MESSAGE, this.clientId, to);
    }

    private void init() {
        group = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();
        bootstrap.group(group);
        collector = new ClientMessageCollector();
        bootstrap.channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipe = ch.pipeline();
                pipe.addLast(new MessageDecoder());
                pipe.addLast(new MessageEncoder());
                pipe.addLast(collector);
            }

        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = bootstrap.connect(ip, port).syncUninterruptibly();
        f.channel().closeFuture().addListener(future -> close());
    }

    public void close() {
        group.shutdownGracefully();
    }
}
