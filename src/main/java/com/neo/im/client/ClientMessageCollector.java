package com.neo.im.client;

import cn.hutool.core.util.ObjectUtil;
import com.neo.im.common.payload.Message;
import com.neo.im.common.tranform.MessageInput;
import com.neo.im.common.tranform.MessageOutput;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
@Slf4j
public class ClientMessageCollector extends SimpleChannelInboundHandler<MessageInput> {
    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.context = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client caught a exception..", cause);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        Message payload = msg.getPayload(Message.class);
        System.out.println("client receiveMessage:" + payload.getContent());
    }

    public void send(MessageOutput message) {
        ChannelHandlerContext ctx = this.context;
        if (ObjectUtil.isNotNull(ctx) && ctx.channel().isActive()) {
            ctx.channel().eventLoop().execute(() -> this.context.writeAndFlush(message));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client channel inActive...{}", ctx.name());
        super.channelInactive(ctx);
    }
}
