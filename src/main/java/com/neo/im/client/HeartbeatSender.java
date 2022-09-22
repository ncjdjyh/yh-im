package com.neo.im.client;

import cn.hutool.core.date.DateUtil;
import com.neo.im.client.config.ClientChatInfo;
import com.neo.im.common.Constant;
import com.neo.im.common.payload.Heartbeat;
import com.neo.im.common.tranform.MessageInput;
import com.neo.im.common.tranform.MessageOutput;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Slf4j
public class HeartbeatSender extends SimpleChannelInboundHandler<MessageInput> {
    private ChannelHandlerContext context;
    ClientChatInfo clientChatInfo = ClientChatInfo.getInstance(clientId);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageInput messageInput) throws Exception {
        // do nothing..
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
    }

    private void sendHeartbeat(ChannelHandlerContext ctx, MessageOutput messageOutput) {
        log.debug("send heartbeat to presence server...:{}", DateUtil.now());
        ctx.channel().writeAndFlush(messageOutput);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;

        if (event.state() == IdleState.ALL_IDLE) {
            log.info("trigger all idle event... context name:{}", ctx.name());
            sendHeartbeat(ctx, new MessageOutput(Constant.Command.MESSAGE, new Heartbeat(clientChatInfo.getClientId(), "ping")));
        }
    }

    public void send(MessageOutput output) {
        sendHeartbeat(this.context, output);
    }
}
