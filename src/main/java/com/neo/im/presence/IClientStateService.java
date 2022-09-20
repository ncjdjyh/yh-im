package com.neo.im.presence;

import com.neo.im.common.HostAddress;
import com.neo.im.presence.entity.command.LoginCommand;

/**
 * @author ncjdjyh
 * @since 2022/9/4
 */
public interface IClientStateService {
    void activeState(LoginCommand command);

    void activeState(Long id);

    HostAddress getConnectedServer(Long id);

    void inActiveState(Long id);

    UserState getState(Long id);
}
