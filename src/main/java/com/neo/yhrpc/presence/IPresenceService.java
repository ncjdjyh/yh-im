package com.neo.yhrpc.presence;

import com.neo.yhrpc.common.HostAddress;

/**
 * @author: ncjdjyh
 * @since: 2022/9/4
 */
public interface IPresenceService {
    boolean activeUserState(Long clientId, HostAddress hostAddress);

    HostAddress getConnectServerAddress(Long clientId);

    boolean inActiveUserState(Long clientId);
}
