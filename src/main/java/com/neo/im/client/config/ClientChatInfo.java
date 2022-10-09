package com.neo.im.client.config;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import com.neo.im.common.HostAddress;
import com.neo.im.common.RegisterCenter;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Data
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@DependsOn({"chatServer", "presenceServer"})
public class ClientChatInfo implements InitializingBean {
    private static ClientChatInfo info;
    private Long clientId;
    private String username;
    private HostAddress chatHostAddress;
    private HostAddress presenceHostAddress;

    @Override
    public String toString() {
        return "ClientChatInfo{" +
                "clientId=" + clientId +
                ", username='" + username + '\'' +
                ", chatHostAddress=" + chatHostAddress +
                ", presenceHostAddress=" + presenceHostAddress +
                '}';
    }

    @Override
    public void afterPropertiesSet() {
        // TODO 重试获取健康连接
        ThreadUtil.safeSleep(500);
        ClientChatInfo clientChatInfo = new ClientChatInfo();
        clientChatInfo.setUsername(RandomUtil.randomString(8));
        clientChatInfo.setChatHostAddress(RegisterCenter.getChatHostAddress());
        clientChatInfo.setPresenceHostAddress(RegisterCenter.getPresenceHostAddress());
    }
}
