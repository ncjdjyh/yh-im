package com.neo.im.common.tranform;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * TODO 编码解码考虑是否可以优化
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
public class MessageDecoder extends ReplayingDecoder<MessageInput> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        String requestId = readStr(in);
        String type = readStr(in);
        String payload = readStr(in);
        out.add(new MessageInput(requestId, type, payload));
    }

    private String readStr(ByteBuf in) {
        int len = in.readInt();
        if (len < 0 || len > (1 << 20)) {
            throw new DecoderException("string too long len=" + len);
        }
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        return new String(bytes, CharsetUtil.UTF_8);
    }
}
