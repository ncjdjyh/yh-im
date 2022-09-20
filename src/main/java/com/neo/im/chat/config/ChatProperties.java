package com.neo.im.chat.config;

import cn.hutool.core.net.NetUtil;
import com.neo.im.common.HostAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ncjdjyh
 * @since 2022/9/14
 */
@Configuration
public class ChatProperties {
    @Value("${chat.server.port}")
    private Integer chatPort;
    @Value("${server.port}")
    private Integer httpPort;

    @Bean("chatServerHostAddress")
    public HostAddress chatServerHostAddress() {
        return new HostAddress(NetUtil.getLocalhostStr(), httpPort, chatPort);
    }
}
