package com.neo.yhrpc.server;

import com.neo.yhrpc.common.MessageDecoder;
import com.neo.yhrpc.common.MessageEncoder;
import com.neo.yhrpc.common.MessageOutput;
import com.neo.yhrpc.common.RequestId;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
public class IMServer {
    private String ip;
    private int port;
    private ServerMessageCollector collector;

    public IMServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
        collector = new ServerMessageCollector();
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
            ChannelFuture c = bootstrap.bind(ip, port).syncUninterruptibly();
            System.out.println("service at " + ip + ":" + port);
            c.channel().closeFuture().syncUninterruptibly();
        } finally {
            System.out.println("service over");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
