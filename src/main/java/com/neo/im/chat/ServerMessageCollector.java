package com.neo.im.chat;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.neo.im.chat.rpc.PresenceService;
import com.neo.im.common.*;
import com.neo.im.common.exception.BizException;
import com.neo.im.common.payload.Message;
import com.neo.im.common.tranform.MessageInput;
import com.neo.im.common.tranform.MessageOutput;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
@Service
@Slf4j
@ChannelHandler.Sharable
public class ServerMessageCollector extends SimpleChannelInboundHandler<MessageInput> {
    @Autowired
    private HostAddress chatServerHostAddress;

    private final ChannelContextHolder channelHolder = new ChannelContextHolder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        Message message = msg.getPayload(Message.class);
        if (message != null) {
            Long messageFrom = message.getMessageFrom();
            if (msg.getType().equals(Constant.Command.LOGIN)) {
                channelHolder.put(messageFrom, ctx);
            }
            if (msg.getType().equals(Constant.Command.LOGOUT)) {
                channelHolder.remove(ctx);
            }
            if (msg.getType().equals(Constant.Command.MESSAGE)) {
                sendMessage(message);
            }
            log.info("channel read message... type:{} content:{}", msg.getType(), message.getContent());
            return;
        }
        log.info("message is empty!");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught a exception...", cause);
    }

    private void sendMessage(Message message) {
        Long messageTo = message.getMessageTo();
        HostAddress toAddress = PresenceService.getConnectedServer(messageTo);
        if (ObjectUtil.isNull(toAddress)) {
            channelHolder.remove(messageTo);
            log.info("{}:当前用户不在线", messageTo);
            return;
        }

        boolean connectToCurrentServer = toAddress.sameChatHostAddress(chatServerHostAddress);
        if (connectToCurrentServer) {
            sendMessageToChannel(message);
        } else {
            String params = JSONUtil.toJsonStr(message, JSONConfig.create().setDateFormat(DatePattern.NORM_DATETIME_PATTERN));
            HttpUtil.post(toAddress.getUrl() + "/api/sendMessage", params);
        }
    }

    public void sendMessageToChannel(Message message) {
        Long messageTo = message.getMessageTo();
        Channel channel = channelHolder.get(messageTo);
        if (ObjectUtil.isNull(channel)) {
            PresenceService.logout(messageTo);
            throw new BizException("找不到可用连接: " + messageTo);
        }
        channel.eventLoop().execute(() -> {
            channel.writeAndFlush(new MessageOutput(message.getMessageId(), Constant.Command.MESSAGE, message));
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channelInactive:{}", ctx.name());
        channelHolder.remove(ctx);
        super.channelInactive(ctx);
    }
}
