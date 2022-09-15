package com.neo.im.server.config;

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
    @Value("${im.server.host}")
    private String ip;
    @Value("${im.server.port}")
    private Integer imConnectPort;
    @Value("${server.port}")
    private Integer httpPort;

    @Bean
    public HostAddress currentServerHostAddress() {
        return new HostAddress(ip, imConnectPort, httpPort);
    }
}
