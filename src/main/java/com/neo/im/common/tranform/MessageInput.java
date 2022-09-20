package com.neo.im.common.tranform;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author neo
 * @FirstInitial 2019/7/13
 * @Description ~
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageInput {
    private String requestId;
    private String type;
    private String payload;

    public <T> T getPayload(Class<T> clazz) {
        if (payload == null) {
            return null;
        }
        return JSONUtil.toBean(payload, clazz);
    }
}