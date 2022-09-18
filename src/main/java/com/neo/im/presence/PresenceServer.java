package com.neo.im.presence;

import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
import com.neo.im.common.RegisterCenter;
import com.neo.im.common.tranform.MessageDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author ncjdjyh
 * @since 2022/9/17
 */
@Service
@Slf4j
public class PresenceServer {
    @Autowired
    @Qualifier("presenceServerHostAddress")
    private HostAddress hostAddress;
    @Autowired
    private HeartbeatReceiver heartbeatReceiver;

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
                        p.addLast(new IdleStateHandler(0, 0, 30));
                        p.addLast(new MessageDecoder());
                        p.addLast(heartbeatReceiver);
                    }
                });
        ChannelFuture f = bootstrap.bind(hostAddress.getIp(), hostAddress.getChatPort()).syncUninterruptibly();
        if (f.isSuccess()) {
            // TODO 订阅服务失效事件
            RegisterCenter.register(hostAddress.getIp(), hostAddress.getChatPort(), Constant.ServiceName.PRESENCE_SERVICE);
            RegisterCenter.register(hostAddress.getIp(), hostAddress.getApiPort(), Constant.ServiceName.PRESENCE_API_SERVICE);
            log.info("presence service at " + hostAddress.getIp() + ":" + hostAddress.getChatPort());
            f.channel().closeFuture().addListener(future -> {
                log.info("presence service over...");
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            });
        } else {
            log.error("start error...");
        }
    }
}
