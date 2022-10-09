package com.neo.im.client.handler;

import com.neo.im.client.config.ClientChatInfo;
import com.neo.im.client.connect.ConnectTaskHandler;
import com.neo.im.common.Constant;
import com.neo.im.common.payload.Heartbeat;
import com.neo.im.common.tranform.MessageInput;
import com.neo.im.common.tranform.MessageOutput;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Slf4j
@Service
public class HeartbeatSender extends SimpleChannelInboundHandler<MessageInput> {
    private ChannelHandlerContext context;
    private final ClientChatInfo clientChatInfo;
    private final ConnectTaskHandler connectTaskHandler;

    private boolean connected = true;

    public HeartbeatSender(ClientChatInfo clientChatInfo, @Lazy ConnectTaskHandler connectTaskHandler) {
        this.clientChatInfo = clientChatInfo;
        this.connectTaskHandler = connectTaskHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageInput messageInput) throws Exception {
        // do nothing..
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
    }

    private void sendHeartbeat(ChannelHandlerContext ctx, MessageOutput messageOutput) {
//        log.debug("send heartbeat to presence server...:{}", DateUtil.now());
        ctx.channel().writeAndFlush(messageOutput);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;

        if (event.state() == IdleState.ALL_IDLE) {
            sendHeartbeat(ctx, new MessageOutput(Constant.MessageType.HEARTBEAT, new Heartbeat(clientChatInfo.getClientId(), "ping")));
        }
    }

    public boolean send(MessageOutput output) {
        // TODO 处理消息发送成功失败
        sendHeartbeat(this.context, output);
        return true;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.connected = false;
        connectTaskHandler.reconnect();
        super.channelInactive(ctx);
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
