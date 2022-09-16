package com.neo.im.presence;

import cn.hutool.core.util.ObjectUtil;
import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ncjdjyh
 * @since 2022/9/4
 */
@Service(value = "memoryPresenceService")
public class MemoryPresenceServiceImpl implements IPresenceService {
    private final Map<Long, UserState> table = new ConcurrentHashMap<>();

    @Override
    public void activeUserState(Long clientId, HostAddress hostAddress) {
        if (ObjectUtil.isNotNull(clientId)) {
            if (table.containsKey(clientId)) {
                activeUserState(clientId);
            } else {
                table.put(clientId, new UserState(Constant.PresenceState.ONLINE, hostAddress));
            }
        }
    }

    @Override
    public void activeUserState(Long clientId) {
        if (ObjectUtil.isNotNull(clientId)) {
            if (table.containsKey(clientId)) {
                UserState userState = table.get(clientId);
                userState.setState(Constant.PresenceState.ONLINE);
            }
        }
    }

    @Override
    public HostAddress getConnectedServer(Long clientId) {
        UserState userState = table.get(clientId);
        if (ObjectUtil.isNotNull(userState) && userState.online()) {
            return userState.getConnectedServer();
        }
        return null;
    }

    @Override
    public void inActiveUserState(Long clientId) {
        if (ObjectUtil.isNotNull(clientId)) {
            UserState userState = table.get(clientId);
            if (ObjectUtil.isNotNull(userState)) {
                userState.setState(Constant.PresenceState.OFFLINE);
            }
        }
    }
}
