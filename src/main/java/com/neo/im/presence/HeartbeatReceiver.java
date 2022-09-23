package com.neo.im.presence;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.neo.im.common.ChannelContextHolder;
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
    private IUserStateService clientStateService;

    private final ChannelContextHolder holder = new ChannelContextHolder();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        IdleStateEvent event = (IdleStateEvent) evt;
        if (event.state() == IdleState.ALL_IDLE) {
            log.info("send heart beat context name:{}", ctx.name());
            Long id = holder.peekClient(ctx);
            if (ObjectUtil.isNotNull(id) && clientStateService.logout(id)) {
                // 保证客户端状态已下线, 再关闭连接
                holder.remove(ctx);
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageInput msg) {
        if (ObjectUtil.isNotNull(msg)) {
            Heartbeat heartBeat = msg.getPayload(Heartbeat.class);
            log.debug("receive heartbeat.. content:{}", heartBeat.getContent());
            if (msg.getType().equals(Constant.MessageType.LOGIN)) {
                log.info("client login: {}", heartBeat.getUserId());
                HostAddress hostAddress = JSONUtil.toBean(heartBeat.getContent(), HostAddress.class);
                Long clientId = heartBeat.getUserId();
                clientStateService.login(clientId, hostAddress);
                holder.put(clientId, ctx);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exception caught", cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        holder.remove(ctx);
        super.channelInactive(ctx);
    }
}
