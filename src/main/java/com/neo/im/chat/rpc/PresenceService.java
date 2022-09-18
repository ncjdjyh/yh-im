package com.neo.im.chat.rpc;

import com.neo.im.common.HostAddress;
import org.springframework.stereotype.Service;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Service
public class PresenceService {
    public HostAddress getConnectedServer(Long messageTo) {
    }

    public void login(Long messageFrom, HostAddress chatServerHostAddress) {
    }

    public void logout(Long messageFrom) {
    }
}
