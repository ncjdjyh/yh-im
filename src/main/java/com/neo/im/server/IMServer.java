package com.neo.im.server;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.neo.im.common.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
@Service
@Slf4j
public class IMServer {
    @Autowired
    private ServerMessageCollector collector;
    @Autowired
    private HostAddress hostAddress;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void bootstrap() {
        executorService.submit(this::start);
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
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
            String ip = hostAddress.getIp();
            int port = hostAddress.getPort();
            ChannelFuture c = bootstrap.bind(ip, port).syncUninterruptibly();
            RegisterCenter.register(hostAddress);
            log.info("service at " + ip + ":" + port);
            c.channel().closeFuture().syncUninterruptibly();
        } finally {
            log.info("service over...");
            RegisterCenter.deregister(hostAddress);
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void sendMessage(Message message) {
        collector.sendMessageToChannel(message);
    }
}
