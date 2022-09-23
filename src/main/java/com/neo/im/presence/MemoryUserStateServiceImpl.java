package com.neo.im.presence;

import cn.hutool.core.util.ObjectUtil;
import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ncjdjyh
 * @since 2022/9/4
 */
@Service(value = "memoryClientStateService")
@Slf4j
public class MemoryUserStateServiceImpl implements IUserStateService {
    private final Map<Long, UserState> table = new ConcurrentHashMap<>();

    @Override
    public boolean login(Long userId) {
        if (ObjectUtil.isNotNull(userId)) {
            if (table.containsKey(userId)) {
                UserState userState = table.get(userId);
                userState.setState(Constant.PresenceState.ONLINE);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean login(Long clientId, HostAddress chatServer) {
        if (ObjectUtil.isNotNull(clientId)) {
            if (table.containsKey(clientId)) {
                UserState userState = table.get(clientId);
                userState.setState(Constant.PresenceState.ONLINE);
            } else {
               table.put(clientId, new UserState(Constant.PresenceState.ONLINE, chatServer));
            }
            return true;
        }
        return false;
    }

    @Override
    public HostAddress getConnectedServer(Long id) {
        UserState userState = table.get(id);
        if (ObjectUtil.isNotNull(userState) && userState.online()) {
            return userState.getConnectedServer();
        }
        return null;
    }

    @Override
    public boolean logout(Long userId) {
        if (ObjectUtil.isNotNull(userId)) {
            UserState userState = table.get(userId);
            if (ObjectUtil.isNotNull(userState)) {
                userState.setState(Constant.PresenceState.OFFLINE);
                return true;
            }
        }
        return false;
    }

}
