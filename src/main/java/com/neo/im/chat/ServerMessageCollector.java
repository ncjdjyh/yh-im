package com.neo.im.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.neo.im.chat.rpc.PresenceService;
import com.neo.im.common.*;
import com.neo.im.common.exception.BizException;
import com.neo.im.common.payload.GroupMessage;
import com.neo.im.common.payload.Heartbeat;
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

import java.util.List;

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
    private ChannelContextHolder channelHolder;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        if (msg.getType().equals(Constant.MessageType.LOGIN)) {
            Heartbeat heartbeat = msg.getPayload(Heartbeat.class);
            channelHolder.put(heartbeat.getUserId(), ctx);
        }
        if (msg.getType().equals(Constant.MessageType.CHAT)) {
            Message message = msg.getPayload(Message.class);
            sendMessage(message);
        }
        if (msg.getType().equals(Constant.MessageType.GROUP_CHAT)) {
            GroupMessage message = msg.getPayload(GroupMessage.class);
            sendMessageGroupMessage(message);
        }
        log.info("channel read message... type:{}", msg.getType());
    }

    private void sendMessageGroupMessage(GroupMessage message) {
        List<Long> users = findUsersByChannel(message.getChannelId());
        if (CollUtil.isNotEmpty(users)) {
            for (Long userId : users) {
                HostAddress toAddress = PresenceService.getConnectedServer(userId);
                if (ObjectUtil.isNull(toAddress)) {
                    channelHolder.remove(userId);
                    log.info("{}:当前用户不在线", userId);
                    return;
                }
                boolean connectToCurrentServer = toAddress.sameChatHostAddress(chatServerHostAddress);
                if (false) {
                    sendGroupMessageToChannel(userId, message);
                } else {
                    JSONObject parseObj = JSONUtil.parseObj(message);
                    parseObj.set("messageTo", userId);
                    String params = JSONUtil.toJsonStr(parseObj, JSONConfig.create().setDateFormat(DatePattern.NORM_DATETIME_PATTERN));
                    HttpUtil.post(toAddress.getUrl() + "/api/sendGroupMessage", params);
                }
            }
        }
    }

    public void sendGroupMessageToChannel(Long messageTo, GroupMessage message) {
        Channel channel = channelHolder.get(messageTo);
        if (ObjectUtil.isNull(channel)) {
            PresenceService.logout(messageTo);
            throw new BizException("找不到可用连接: " + messageTo);
        }
        channel.eventLoop().execute(() -> {
            channel.writeAndFlush(new MessageOutput(message.getMessageId(), Constant.MessageType.GROUP_CHAT, message));
        });
    }

    private List<Long> findUsersByChannel(Long channelId) {
        return CollUtil.newArrayList(1L, 2L, 3L);
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
            channel.writeAndFlush(new MessageOutput(message.getMessageId(), Constant.MessageType.CHAT, message));
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channelInactive:{}", ctx.name());
        channelHolder.remove(ctx);
        super.channelInactive(ctx);
    }
}
