package com.neo.yhrpc.presence;

import cn.hutool.core.util.ObjectUtil;
import com.neo.yhrpc.common.Constant;
import com.neo.yhrpc.common.HostAddress;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: ncjdjyh
 * @since: 2022/9/4
 */
public class MemoryPresenceService implements IPresenceService {
    private final Map<Long, UserState> table = new ConcurrentHashMap<>();

    @Override
    public boolean activeUserState(Long clientId, HostAddress hostAddress) {
        if (ObjectUtil.isNotNull(clientId)) {
            table.put(clientId, new UserState(Constant.PresenceState.ONLINE, hostAddress));
            return true;
        }
        return false;
    }

    @Override
    public HostAddress getConnectServerAddress(Long clientId) {
        UserState userState = table.get(clientId);
        if (ObjectUtil.isNotNull(userState) && userState.online()) {
            return userState.getConnectedServer();
        }
        return null;
    }

    @Override
    public boolean inActiveUserState(Long clientId) {
        if (ObjectUtil.isNotNull(clientId)) {
            UserState userState = table.get(clientId);
            if (ObjectUtil.isNotNull(userState)) {
                userState.setState(Constant.PresenceState.OFFLINE);
                return true;
            }
        }
        return false;
    }
}
