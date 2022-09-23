package com.neo.im.common;

/**
 * @author ncjdjyh
 * @since 2022/8/28
 */
public class Constant {
    public interface MessageType {
        String LOGIN = "login";
        String LOGOUT = "logout";
        String CHAT = "singleChat";
        String GROUP_CHAT = "groupChat";
        String HEARTBEAT = "heartbeat";
    }

    public interface PresenceState {
        Integer OFFLINE = 0;
        Integer ONLINE = 1;
    }

    public interface ServiceName {
        String CHAT_SERVICE = "CHAT_SERVICE";
        String CHAT_API_SERVICE = "CHAT_API_SERVICE";
        String PRESENCE_SERVICE = "PRESENCE_SERVICE";
        String PRESENCE_API_SERVICE = "PRESENCE_API_SERVICE";
    }
}
