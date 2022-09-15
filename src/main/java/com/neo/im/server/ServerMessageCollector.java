package com.neo.im.server;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.common.utils.DateFormatUtils;
import com.neo.im.common.*;
import com.neo.im.presence.IPresenceService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
@Service
@Slf4j
@ChannelHandler.Sharable
public class ServerMessageCollector extends SimpleChannelInboundHandler<MessageInput> {
    @Autowired
    @Qualifier("memoryPresenceService")
    private IPresenceService presenceService;
    @Autowired
    private HostAddress hostAddress;

    private final Map<Long, ChannelHandlerContext> channelMap = new ConcurrentHashMap<>(10);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        Message message = msg.getPayload(Message.class);
        if (message != null) {
            Long messageFrom = message.getMessageFrom();
            if (StrUtil.equals(msg.getType(), Constant.Command.LOGIN)) {
                channelMap.put(messageFrom, ctx);
                presenceService.activeUserState(messageFrom, getHostAddress());
            }
            if (StrUtil.equals(msg.getType(), Constant.Command.LOGOUT)) {
                channelMap.remove(messageFrom);
            }
            if (StrUtil.equals(msg.getType(), Constant.Command.MESSAGE)) {
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

        boolean connectToCurrentServer = toAddress.sameConnectServer(getHostAddress());
        if (connectToCurrentServer) {
            sendMessageToChannel(message);
        } else {
            String params = JSONUtil.toJsonStr(message, JSONConfig.create().setDateFormat(DatePattern.NORM_DATETIME_PATTERN));
            HttpUtil.post(toAddress.getHttpUrl() + "/api/sendMessage", params);
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
                presenceService.inActiveUserState(k);
            }
        });
        super.channelInactive(ctx);
    }

    public HostAddress getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(HostAddress hostAddress) {
        this.hostAddress = hostAddress;
    }
}
