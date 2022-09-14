package com.neo.im.client;

import com.neo.im.common.*;
import com.neo.im.common.MessageInput;
import com.neo.im.common.MessageOutput;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
public class ClientMessageCollector extends SimpleChannelInboundHandler<MessageInput> {
    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.context = ctx;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        Message payload = msg.getPayload(Message.class);
        System.out.println("client receiveMessage:" + payload.getContent());
    }

    public void send(MessageOutput message) {
        ChannelHandlerContext ctx = this.context;
        if (ctx != null) {
            ctx.channel().eventLoop().execute(() -> this.context.writeAndFlush(message));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
