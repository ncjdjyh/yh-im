package com.neo.im.presence;

import com.neo.im.common.HostAddress;

/**
 * @author ncjdjyh
 * @since 2022/9/4
 */
public interface IUserStateService {
    boolean login(Long userId);

    boolean login(Long userId, HostAddress chatServer);

    boolean logout(Long userId);

    HostAddress getConnectedServer(Long id);
}
