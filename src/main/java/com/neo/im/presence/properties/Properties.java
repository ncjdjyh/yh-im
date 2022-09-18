package com.neo.im.presence.properties;

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
public class Properties {
    @Value("${presence.server.port}")
    private Integer presencePort;
    @Value("${server.port}")
    private Integer httpPort;

    @Bean("presenceServerHostAddress")
    public HostAddress presenceServerHostAddress() {
        return new HostAddress(NetUtil.getLocalhostStr(), httpPort, presencePort);
    }
}
