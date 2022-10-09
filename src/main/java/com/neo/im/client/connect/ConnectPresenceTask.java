package com.neo.im.client.connect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neo.im.client.handler.HeartbeatSender;
import com.neo.im.client.config.ClientChatInfo;
import com.neo.im.common.Constant;
import com.neo.im.common.RegisterCenter;
import com.neo.im.common.exception.BizException;
import com.neo.im.common.payload.Heartbeat;
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
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * @author ncjdjyh
 * @since 2022/10/5
 */
@Component
public class ConnectPresenceTask implements Callable<Boolean> {
    @Autowired
    private HeartbeatSender heartbeatSender;
    @Autowired
    ClientChatInfo clientChatInfo;

    @Override
    public Boolean call() {
        if (heartbeatSender.isConnected()) {
            return true;
        }
        Instance instance = RegisterCenter.getOneHealthyInstance(Constant.ServiceName.PRESENCE_SERVICE);
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
                // 3s 发送一次心跳
                pipe.addLast(new IdleStateHandler(0, 0, 3));
                pipe.addLast(new MessageEncoder());
                pipe.addLast(heartbeatSender);
            }

        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture f = bootstrap.connect(clientChatInfo.getPresenceHostAddress().getIp(), clientChatInfo.getPresenceHostAddress().getChatPort()).syncUninterruptibly();
        f.channel().closeFuture().addListener(future -> group.shutdownGracefully());

        Heartbeat heartbeat = new Heartbeat(clientChatInfo.getClientId(), JSONUtil.toJsonStr(clientChatInfo.getChatHostAddress()));
        MessageOutput output = new MessageOutput(Constant.MessageType.LOGIN, heartbeat);
        boolean connectSuccess = f.isSuccess() && heartbeatSender.send(output);

        if (connectSuccess) {
            heartbeatSender.setConnected(true);
        }

        return connectSuccess;
    }
}
