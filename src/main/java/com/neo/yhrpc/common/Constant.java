package com.neo.yhrpc.common;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
public class Constant {
    public interface Command {
        String LOGIN = "login";
        String LOGOUT = "logout";
        String MESSAGE = "message";
    }

    public interface PresenceState {
        Integer OFFLINE = 0;
        Integer ONLINE = 1;
    }
}
