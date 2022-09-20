package com.neo.im.chat.rpc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.neo.im.common.Constant;
import com.neo.im.common.HostAddress;
import com.neo.im.common.RegisterCenter;
import com.neo.im.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Slf4j
public class PresenceService {
    private static String buildUrl(String ip, int port, String path) {
        return String.format("%s:%s/%s", ip, port, path);
    }

    public static HostAddress getConnectedServer(Long id) {
        Instance hostAddress = RegisterCenter.getOneHealthyInstance(Constant.ServiceName.PRESENCE_API_SERVICE);
        Map<String, Object> params = Collections.singletonMap("id", id);

        HttpResponse response = HttpRequest
                .get(buildUrl(hostAddress.getIp(), hostAddress.getPort(), "getUserConnectHostAddress"))
                .form(params)
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_OK) {
            String body = response.body();
            if (StrUtil.isNotBlank(body)) {
                return JSONUtil.toBean(body, HostAddress.class);
            }
            return null;
        }

        throw new BizException("PresenceService exception:" + response.body());
    }

    public static void login(Long id, HostAddress chatServerHostAddress) {
        Instance hostAddress = RegisterCenter.getOneHealthyInstance(Constant.ServiceName.PRESENCE_API_SERVICE);
        Map<String, Object> params = new HashMap<>(2);
        params.put("id", id);
        params.put("hostAddress", JSONUtil.toJsonStr(chatServerHostAddress));
        String requestBody = JSONUtil.toJsonStr(params);
        log.info("login requestBody:{}", requestBody);
        HttpResponse response = HttpRequest
                .post(buildUrl(hostAddress.getIp(), hostAddress.getPort(), "login"))
                .body(requestBody)
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_OK) {
            return;
        }

        throw new BizException("PresenceService exception:" + response.body());
    }

    public static void logout(Long messageFrom) {
        Instance hostAddress = RegisterCenter.getOneHealthyInstance(Constant.ServiceName.PRESENCE_API_SERVICE);
        Map<String, Object> params = Collections.singletonMap("id", messageFrom);

        HttpResponse response = HttpRequest
                .post(buildUrl(hostAddress.getIp(), hostAddress.getPort(), "logout"))
                .form(params)
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_OK) {
            return;
        }

        throw new BizException("PresenceService exception:" + response.body());
    }
}
