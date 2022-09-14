package com.neo.im.presence;

import com.neo.im.common.HostAddress;

/**
 * @author: ncjdjyh
 * @since: 2022/9/4
 */
public interface IPresenceService {
    void activeUserState(Long clientId, HostAddress hostAddress);

    void activeUserState(Long clientId);

    HostAddress getConnectedServer(Long clientId);

    boolean inActiveUserState(Long clientId);
}
