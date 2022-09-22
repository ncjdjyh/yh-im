package com.neo.im.chat.rpc;

import com.neo.im.common.HostAddress;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * @author ncjdjyh
 * @since 2022/9/20
 */
public class PresenceServiceTest {
    @Test
    public void buildUrl() {
        HostAddress connectedServer = PresenceService.getConnectedServer(1L);
    }

    @Test
    public void getConnectedServer() {
    }

    @Test
    public void login() {
    }

    @Test
    public void logout() {
    }
}