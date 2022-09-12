package com.neo.yhrpc.common;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neo.yhrpc.client.IMClient;
import com.neo.yhrpc.server.IMServer;

/**
 * @author: ncjdjyh
 * @since: 2022/8/18
 */
public class RegisterCenter {
    private static final String serverList = "127.0.0.1:8848";
    private static String SERVICE_NAME = "IM_SERVICE";

    private RegisterCenter() {
    }

    public static NamingService createNamingService() {
        try {
            return NamingFactory.createNamingService(serverList);
        } catch (NacosException e) {
            throw new BizException("can not get nacos namingService", e);
        }
    }

    public static void register(IMServer rpcProvider) {
        try {
            createNamingService().registerInstance(SERVICE_NAME, rpcProvider.getIp(), rpcProvider.getPort());
        } catch (NacosException e) {
            throw new BizException("register rpcProvider exception!", e);
        }
    }

    public static void deregister(IMServer rpcProvider) {
        try {
            createNamingService().deregisterInstance(SERVICE_NAME, rpcProvider.getIp(), rpcProvider.getPort());
        } catch (NacosException e) {
            throw new BizException("deregister rpcProvider exception!", e);
        }
    }

    public static Instance getInstance() {
        try {
            return createNamingService().selectOneHealthyInstance(SERVICE_NAME);
        } catch (NacosException e) {
            throw new BizException("get service instance exception!", e);
        }
    }
}
