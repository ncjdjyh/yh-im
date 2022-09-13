package com.neo.yhrpc.server;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.neo.yhrpc.common.*;
import com.neo.yhrpc.presence.IPresenceService;
import com.neo.yhrpc.presence.MemoryPresenceService;
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
    private HostAddress serverHostAddress;
    private IPresenceService presenceService;

    public ServerMessageCollector(HostAddress serverHostAddress) {
        this.serverHostAddress = serverHostAddress;
        presenceService = new MemoryPresenceService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        Message message = msg.getPayload(Message.class);
        if (message != null) {
            Long messageFrom = message.getMessageFrom();
            Long messageTo = message.getMessageTo();
            if (StrUtil.equals(msg.getType(), Constant.Command.LOGIN)) {
                channelMap.put(messageFrom, ctx);
                presenceService.activeUserState(messageFrom, getServerHostAddress());
            }
            if (StrUtil.equals(msg.getType(), Constant.Command.LOGOUT)) {
                channelMap.remove(messageFrom);
            }
            if (StrUtil.equals(msg.getType(), Constant.Command.MESSAGE)) {
                HostAddress hostAddress = presenceService.getConnectServerAddress(messageTo);
                if (ObjectUtil.isNull(hostAddress)) {
                    System.out.println(messageTo + "当前不在线");
                    return;
                }
                sendMessageToHostAddress(hostAddress, message);
            }
            System.out.println("server receiveMessage:" + msg.getType() + "&" + message.getContent());
            return;
        }
        System.out.println("message is empty!");
    }

    private void sendMessageToHostAddress(HostAddress hostAddress, Message message) {
        // TODO 考虑在服务端做消息分发是否合理
        JSONObject paramMap = JSONUtil.parseObj(message);
        HttpUtil.post(hostAddress.getUrl() + "/api/sendMessage", paramMap);
    }

    public void sendMessage(MessageInput payload) {
        Message message = payload.getPayload(Message.class);
        ChannelHandlerContext context = channelMap.get(message.getMessageTo());
        if (ObjectUtil.isNull(context)) {
            System.out.println(message.getMessageTo() + "当前不在线");
            return;
        }
        context.channel().eventLoop().execute(() -> {
            context.writeAndFlush(new MessageOutput(payload.getRequestId(), payload.getType(), message));
        });
    }

    public HostAddress getServerHostAddress() {
        return serverHostAddress;
    }

    public void setServerHostAddress(HostAddress serverHostAddress) {
        this.serverHostAddress = serverHostAddress;
    }
}
