package com.neo.im.common;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Slf4j
@Component
public class ChannelContextHolder {
    private final Map<Long, Channel> holder = new ConcurrentHashMap<>(16);

    public void put(Long id, ChannelHandlerContext context) {
        holder.put(id, context.channel());
    }

    public Channel get(Long id) {
        return holder.get(id);
    }

    public Map<Long, Channel> getHolder() {
        return holder;
    }

    public Long peekClient(ChannelHandlerContext context) {
        for (Map.Entry<Long, Channel> entry : holder.entrySet()) {
            if (entry.getValue() == context) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void remove(ChannelHandlerContext context) {
        for (Map.Entry<Long, Channel> entry : holder.entrySet()) {
            if (entry.getValue() == context.channel()) {
                Long id = entry.getKey();
                if (ObjectUtil.isNotNull(id)) {
                    log.info("连接中断 id: {}", id);
                    context.close();
                }
            }
        }
    }

    public void remove(Long id) {
        if (ObjectUtil.isNull(id)) {
            Channel channel = holder.remove(id);
            if (ObjectUtil.isNotNull(channel)) {
                channel.close();
            }
        }
    }
}
