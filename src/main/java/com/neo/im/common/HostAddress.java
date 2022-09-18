package com.neo.im.common;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author ncjdjyh
 * @since 2022/9/4
 */
@Data
@AllArgsConstructor
public class HostAddress implements Serializable {
    private static final long serialVersionUID = 4677120511873972139L;
    private String ip;
    private Integer apiPort;
    private Integer chatPort;

    public boolean sameHostAddress(HostAddress hostAddress) {
        return ObjectUtil.isNotNull(hostAddress)
                && hostAddress.getIp().equals(getIp())
                && hostAddress.getChatPort().equals(getChatPort());
    }

    public String getUrl() {
        return String.format("%s:%s", getIp(), getApiPort());
    }
}
