package com.neo.im.common;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

/**
 * @author ncjdjyh
 * @since 2022/8/18
 */
public class RegisterCenter {
    private static final String serverList = "127.0.0.1:8848";

    private RegisterCenter() {
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
            createNamingService().registerInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new BizException("register rpcProvider exception!", e);
        }
    }

    public static void deregister(String ip, int port, String serviceName) {
        try {
            createNamingService().deregisterInstance(serviceName, ip, port);
        } catch (NacosException e) {
            throw new BizException("deregister rpcProvider exception!", e);
        }
    }

    public static Instance getOneHealthyInstance(String serviceName) {
        try {
            return createNamingService().selectOneHealthyInstance(serviceName);
        } catch (NacosException e) {
            throw new BizException("get service instance exception!", e);
        }
    }
}
