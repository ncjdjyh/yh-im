package com.neo.yhrpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author: ncjdjyh
 * @since: 2022/9/4
 */
@Data
@AllArgsConstructor
public class HostAddress {
    String ip;
    int port;

    public String getUrl() {
        return String.format("%s:%s", getIp(), getPort());
    }
}
