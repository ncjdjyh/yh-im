package com.neo.im.chat;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.neo.im.chat.rpc.PresenceService;
import com.neo.im.common.*;
import com.neo.im.common.payload.Message;
import com.neo.im.common.tranform.MessageInput;
import com.neo.im.common.tranform.MessageOutput;
import com.neo.im.presence.IClientStateService;
import com.neo.im.presence.PresenceServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    @Autowired
    private PresenceService presenceService;

    private final Map<Long, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>(16);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        Message message = msg.getPayload(Message.class);
        if (message != null) {
            Long messageFrom = message.getMessageFrom();
            if (msg.getType() == Constant.Command.LOGIN) {
                channelMap.put(messageFrom, ctx);
                presenceService.login(messageFrom, chatServerHostAddress);
            }
            if (msg.getType() == Constant.Command.LOGOUT) {
                channelMap.remove(messageFrom);
                presenceService.logout(messageFrom);
            }
            if (msg.getType() == Constant.Command.MESSAGE) {
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
        HostAddress toAddress = presenceService.getConnectedServer(messageTo);
        if (ObjectUtil.isNull(toAddress)) {
            channelMap.remove(messageTo);
            log.info("{}:当前用户不在线", messageTo);
            return;
        }

        boolean connectToCurrentServer = toAddress.sameHostAddress(chatServerHostAddress);
        if (connectToCurrentServer) {
            sendMessageToChannel(message);
        } else {
            String params = JSONUtil.toJsonStr(message, JSONConfig.create().setDateFormat(DatePattern.NORM_DATETIME_PATTERN));
            HttpUtil.post(toAddress.getUrl() + "/api/sendMessage", params);
        }
    }

    public void sendMessageToChannel(Message message) {
        ChannelHandlerContext context = channelMap.get(message.getMessageTo());
        if (ObjectUtil.isNull(context)) {
            throw new BizException("找不到可用连接: " + message);
        }
        context.channel().eventLoop().execute(() -> {
            context.writeAndFlush(new MessageOutput(message.getMessageId(), Constant.Command.MESSAGE, message));
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channelInactive:{}", ctx.name());
        channelMap.forEach((k, v) -> {
            if (v == ctx) {
                log.info("remove client connection..id:{}", k);
                channelMap.remove(k);
            }
        });
        super.channelInactive(ctx);
    }
}
