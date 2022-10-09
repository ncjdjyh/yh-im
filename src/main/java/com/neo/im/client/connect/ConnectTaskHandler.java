package com.neo.im.client.connect;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ncjdjyh
 * @since 2022/10/5
 */
@Component
@Slf4j
public class ConnectTaskHandler {
    AtomicInteger threadNumber = new AtomicInteger(1);
    private final ExecutorService executorService = Executors.newCachedThreadPool(task -> new Thread(task, "connectTask" + "-" + threadNumber.getAndIncrement()));

    @Autowired
    private ConnectChatTask connectChatTask;
    @Autowired
    private ConnectPresenceTask connectPresenceTask;

    public boolean connect() {
        try {
            return executorService.submit(connectChatTask).get(3000, TimeUnit.SECONDS)
                    && executorService.submit(connectPresenceTask).get(3000, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("连接失败", e);
            return false;
        }
    }

    public void reconnect() {
        log.info("start reconnect...");
        while (true) {
            ThreadUtil.safeSleep(3000);
            if (connect()) {
                log.info("reconnected...");
                return;
            }
        }
    }
}
