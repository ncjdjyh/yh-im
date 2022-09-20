package com.neo.im.chat;

import com.alibaba.nacos.api.naming.NamingService;
import com.neo.im.common.*;
import com.neo.im.common.payload.Message;
import com.neo.im.common.tranform.MessageDecoder;
import com.neo.im.common.tranform.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


/**
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
@Service
@Slf4j
public class ChatServer {
    @Autowired
    private ServerMessageCollector collector;
    @Autowired
    private HostAddress chatServerHostAddress;

    @PostConstruct
    public void bootstrap() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(io.netty.channel.socket.SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new MessageEncoder());
                        p.addLast(new MessageDecoder());
                        p.addLast(collector);
                    }
                });
        String ip = chatServerHostAddress.getIp();
        int port = chatServerHostAddress.getChatPort();
        ChannelFuture f = bootstrap.bind(ip, port).syncUninterruptibly();
        if (f.isSuccess()) {
            RegisterCenter.register(chatServerHostAddress.getIp(), chatServerHostAddress.getChatPort(), Constant.ServiceName.CHAT_SERVICE);
            RegisterCenter.register(chatServerHostAddress.getIp(), chatServerHostAddress.getApiPort(), Constant.ServiceName.CHAT_API_SERVICE);
            log.info("service at " + ip + ":" + port);
        }
        f.channel().closeFuture().addListener(future -> {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                    log.info("service over...");
                }
        );
    }

    public void sendMessage(Message message) {
        collector.sendMessageToChannel(message);
    }
}
