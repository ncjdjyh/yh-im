package com.neo.im.presence;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Slf4j
public class ChannelContextHolder {
    private final Map<Long, Channel> holder = new ConcurrentHashMap<>(16);

    public void put(Long id, Channel socketChannel) {
        holder.put(id, socketChannel);
    }

    public Channel get(Long id) {
        return holder.get(id);
    }

    public Map<Long, Channel> getHolder() {
        return holder;
    }

    public Long remove(Channel nioSocketChannel) {
        for (Map.Entry<Long, Channel> entry : holder.entrySet()) {
            if (entry.getValue() == nioSocketChannel) {
                log.info("连接中断 id: {}", entry.getKey());
                holder.remove(entry.getKey());
                return entry.getKey();
            }
        }
        return null;
    }
}
