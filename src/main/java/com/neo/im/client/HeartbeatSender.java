package com.neo.im.client;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.neo.im.client.config.ClientInfo;
import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
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
    ClientInfo clientInfo = ClientInfo.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageInput messageInput) throws Exception {
        // do nothing..
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
    }

    public void sendHeartbeat(ChannelHandlerContext ctx, String type) {
        log.debug("send heartbeat to presence server...:{}", DateUtil.now());
        ctx.channel().writeAndFlush(new MessageOutput(type, new Heartbeat(clientInfo.getClientId(), "ping")));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;

        if (event.state() == IdleState.ALL_IDLE) {
            log.info("trigger all idle event... context name:{}", ctx.name());
            sendHeartbeat(this.context, Constant.Command.MESSAGE);
        }
    }
}
