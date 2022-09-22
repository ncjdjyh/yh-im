package com.neo.im.presence;

import cn.hutool.core.util.ObjectUtil;
import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
import com.neo.im.presence.entity.command.LoginCommand;
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
public class MemoryClientStateServiceImpl implements IClientStateService {
    private final Map<Long, UserState> table = new ConcurrentHashMap<>();

    @Override
    public boolean activeState(Long id) {
        if (ObjectUtil.isNotNull(id)) {
            if (table.containsKey(id)) {
                UserState userState = table.get(id);
                userState.setState(Constant.PresenceState.ONLINE);
                return true;
            }
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
    public boolean inActiveState(Long clientId) {
        if (ObjectUtil.isNotNull(clientId)) {
            UserState userState = table.get(clientId);
            if (ObjectUtil.isNotNull(userState)) {
                userState.setState(Constant.PresenceState.OFFLINE);
                return true;
            }
        }
        return false;
    }

    @Override
    public UserState getState(Long id) {
        return table.get(id);
    }
}
