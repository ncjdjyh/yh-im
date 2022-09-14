package com.neo.im.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author: ncjdjyh
 * @since: 2022/9/4
 */
@Data
@AllArgsConstructor
public class HostAddress implements Serializable {
    private static final long serialVersionUID = 4677120511873972139L;
    private String ip;
    private int port;

    public String getUrl() {
        return String.format("%s:%s", getIp(), getPort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HostAddress that = (HostAddress) o;
        return port == that.port &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
