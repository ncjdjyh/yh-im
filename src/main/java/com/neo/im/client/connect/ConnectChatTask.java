package com.neo.im.client.connect;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neo.im.client.handler.ClientMessageCollector;
import com.neo.im.client.config.ClientChatInfo;
import com.neo.im.common.Constant;
import com.neo.im.common.RegisterCenter;
import com.neo.im.common.exception.BizException;
import com.neo.im.common.payload.Heartbeat;
import com.neo.im.common.tranform.MessageDecoder;
import com.neo.im.common.tranform.MessageEncoder;
import com.neo.im.common.tranform.MessageOutput;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * @author ncjdjyh
 * @since 2022/10/5
 */
@Component
public class ConnectChatTask implements Callable<Boolean> {
    @Autowired
    private ClientMessageCollector messageCollector;
    @Autowired
    private ClientChatInfo clientChatInfo;

    @Override
    public Boolean call() {
        if (messageCollector.isConnected()) {
            return true;
        }
        Instance instance = RegisterCenter.getOneHealthyInstance(Constant.ServiceName.CHAT_SERVICE);
        if (ObjectUtil.isNull(instance)) {
            throw new BizException("no available service");
        }

        NioEventLoopGroup group = new NioEventLoopGroup(1);
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
        ChannelFuture f = bootstrap.connect(clientChatInfo.getChatHostAddress().getIp(), clientChatInfo.getChatHostAddress().getChatPort()).syncUninterruptibly();
        f.channel().closeFuture().addListener(future -> group.shutdownGracefully());

        Heartbeat heartbeat = new Heartbeat(clientChatInfo.getClientId(), "login");
        MessageOutput messageOutput = new MessageOutput(Constant.MessageType.LOGIN, heartbeat);
        boolean connectSuccess = (f.isSuccess() && messageCollector.send(messageOutput));
        if (connectSuccess) {
            messageCollector.setConnected(true);
        }
        return connectSuccess;
    }
}
