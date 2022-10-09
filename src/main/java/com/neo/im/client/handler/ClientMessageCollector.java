package com.neo.im.client.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.neo.im.client.connect.ConnectTaskHandler;
import com.neo.im.common.Constant;
import com.neo.im.common.payload.Message;
import com.neo.im.common.payload.GroupMessage;
import com.neo.im.common.tranform.MessageInput;
import com.neo.im.common.tranform.MessageOutput;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
@Slf4j
@Service
public class ClientMessageCollector extends SimpleChannelInboundHandler<MessageInput> {
    private ChannelHandlerContext context;
    private boolean connected = true;

    private final ConnectTaskHandler connectTaskHandler;

    public ClientMessageCollector(@Lazy ConnectTaskHandler connectTaskHandler) {
        this.connectTaskHandler = connectTaskHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.context = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client caught a exception:", cause);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        if (StrUtil.equals(msg.getType(), Constant.MessageType.CHAT)) {
            Message message = msg.getPayload(Message.class);
            log.info("client receiveMessage:{}", message.getContent());
        } else if (StrUtil.equals(msg.getType(), Constant.MessageType.GROUP_CHAT)) {
            GroupMessage message = msg.getPayload(GroupMessage.class);
            log.info("client channelId:{} receiveMessage:{}", message.getChannelId(), message.getContent());
        } else {
            log.warn("未知类型:{}", msg.getType());
        }
    }

    public boolean send(MessageOutput message) {
        // TODO 处理消息发送成功失败
        ChannelHandlerContext ctx = this.context;
        if (ObjectUtil.isNotNull(ctx) && ctx.channel().isActive()) {
            ctx.channel().eventLoop().execute(() -> this.context.writeAndFlush(message));
            return true;
        }
        return false;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("client connect channel inActive...{}", ctx.name());
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
