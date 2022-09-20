package com.neo.im.presence;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
import com.neo.im.common.payload.Heartbeat;
import com.neo.im.common.tranform.MessageInput;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Service
@Slf4j
@ChannelHandler.Sharable
public class HeartbeatReceiver extends SimpleChannelInboundHandler<MessageInput> {
    @Autowired
    IClientStateService userStateService;

    ChannelContextHolder holder = new ChannelContextHolder();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        IdleStateEvent event = (IdleStateEvent) evt;

        if (event.state() == IdleState.ALL_IDLE) {
            log.info("trigger all idle event... context name:{}", ctx.name());
            // TODO 计数, 关闭
            Long id = holder.remove(ctx.channel());
            if (ObjectUtil.isNotNull(id)) {
                ctx.close();
                userStateService.inActiveState(id);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        if (ObjectUtil.isNotNull(msg)) {
            Heartbeat heartBeat = msg.getPayload(Heartbeat.class);
            log.debug("receive heartbeat.. content:{}", heartBeat.getContent());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exception caught", cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        holder.remove(ctx.channel());
        super.channelActive(ctx);
    }
}
