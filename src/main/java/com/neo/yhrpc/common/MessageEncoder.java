package com.neo.yhrpc.common;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
public class MessageEncoder extends MessageToMessageEncoder<MessageOutput> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageOutput msg, List<Object> out) {
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer();
        writeStr(buf, msg.getRequestId());
        writeStr(buf, msg.getType());
        writeStr(buf, JSONUtil.toJsonStr(msg.getPayload()));
        out.add(buf);
    }

    private void writeStr(ByteBuf buf, String s) {
        buf.writeInt(s.length());
        buf.writeBytes(s.getBytes(CharsetUtil.UTF_8));
    }
}

