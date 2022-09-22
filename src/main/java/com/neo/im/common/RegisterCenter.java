package com.neo.im.common;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neo.im.common.exception.BizException;

/**
 * @author ncjdjyh
 * @since 2022/8/18
 */
public class RegisterCenter {
    private static final String serverList = "127.0.0.1:8848";
    private static NamingService namingService;

    static {
        namingService = createNamingService();
    }

    public static NamingService createNamingService() {
        try {
            return NamingFactory.createNamingService(serverList);
        } catch (NacosException e) {
            throw new BizException("can not get nacos namingService", e);
        }
    }

    public static void register(String ip, int port, String serviceName) {
        try {
            namingService.registerInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new BizException("register rpcProvider exception!", e);
        }
    }

    public static void deregister(String ip, int port, String serviceName) {
        try {
            namingService.deregisterInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new BizException("deregister rpcProvider exception!", e);
        }
    }

    public static Instance getOneHealthyInstance(String serviceName) {
        try {
            return namingService.selectOneHealthyInstance(serviceName);
        } catch (NacosException e) {
            throw new BizException("get service instance exception!", e);
        }
    }

    public static HostAddress getChatHostAddress() {
        Instance apiService = getOneHealthyInstance(Constant.ServiceName.CHAT_API_SERVICE);
        Instance chatService = getOneHealthyInstance(Constant.ServiceName.CHAT_SERVICE);
        return new HostAddress(apiService.getIp(), apiService.getPort(), chatService.getPort());
    }

    public static HostAddress getPresenceHostAddress() {
        Instance apiService = getOneHealthyInstance(Constant.ServiceName.PRESENCE_API_SERVICE);
        Instance chatService = getOneHealthyInstance(Constant.ServiceName.PRESENCE_SERVICE);
        return new HostAddress(apiService.getIp(), apiService.getPort(), chatService.getPort());
    }
}
