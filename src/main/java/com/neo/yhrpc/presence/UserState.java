package com.neo.yhrpc.presence;

import com.neo.yhrpc.common.Constant;
import com.neo.yhrpc.common.HostAddress;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: ncjdjyh
 * @since: 2022/9/11
 */
@Data
public class UserState {
    public UserState() {
    }

    public UserState(Integer state, HostAddress connectedServer) {
        this.state = state;
        this.connectedServer = connectedServer;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 0: 离线 1: 在线
     */
    private Integer state;

    /**
     * 上次连接时间, 用于记录心跳
     */
    private LocalDateTime lastActiveTime;

    /**
     * 连接中的 IM 服务器
     */
    private HostAddress connectedServer;

    public boolean online() {
        return getState().equals(Constant.PresenceState.ONLINE);
    }
}
