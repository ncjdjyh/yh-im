package com.neo.yhrpc.server;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.neo.yhrpc.common.Constant;
import com.neo.yhrpc.common.Message;
import com.neo.yhrpc.common.MessageInput;
import com.neo.yhrpc.common.MessageOutput;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
@ChannelHandler.Sharable
public class ServerMessageCollector extends SimpleChannelInboundHandler<MessageInput> {
    private final Map<Long, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>(10);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        Message message = msg.getPayload(Message.class);
        if (message != null) {
            if (StrUtil.equals(msg.getType(), Constant.Command.LOGIN)) {
                Long messageFrom = message.getMessageFrom();
                channelMap.put(messageFrom, ctx);
            }
            if (StrUtil.equals(msg.getType(), Constant.Command.MESSAGE)) {
                sendMessage(msg);
            }
            System.out.println("server receiveMessage:" + msg.getType() + "&" + message.getContent());
            return;
        }
        System.out.println("message is empty!");
    }

    private void sendMessage(MessageInput payload) {
        Message message = payload.getPayload(Message.class);
        ChannelHandlerContext context = channelMap.get(message.getMessageTo());
        if (ObjectUtil.isNull(context)) {
            System.out.println(message.getMessageTo() + "当前不在线");
            return;
        }
        context.writeAndFlush(new MessageOutput(payload.getRequestId(), payload.getType(), message));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    public void send(MessageOutput message, Long to) {
        channelMap.forEach((userId, context) -> {
            if (userId.equals(to)) {
                context.writeAndFlush(message);
            }
        });
    }
}
