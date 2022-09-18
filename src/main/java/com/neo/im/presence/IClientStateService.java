package com.neo.im.presence;

import com.neo.im.common.HostAddress;

/**
 * @author ncjdjyh
 * @since 2022/9/4
 */
public interface IClientStateService {
    void activeState(Long id, HostAddress hostAddress);

    void activeState(Long id);

    HostAddress getConnectedServer(Long id);

    void inActiveState(Long id);

    UserState getState(Long id);
}
