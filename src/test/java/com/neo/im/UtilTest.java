package com.neo.im;

import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.neo.im.common.payload.Message;
import org.junit.Test;

/**
 * @author ncjdjyh
 * @since 2022/9/15
 */

public class UtilTest {
    @Test
    public void testJSONUtil() {
        Message message = new Message(1L, 2L, "hello");
        String s1 = JSONUtil.toJsonStr(message, JSONConfig.create().setDateFormat(DatePattern.NORM_DATETIME_MINUTE_PATTERN));
        String s = JSONUtil.toJsonStr(message);
        System.out.println(s1);
        System.out.println(s);

        Message message1 = JSONUtil.toBean(s, Message.class);
        Message message2 = JSONUtil.toBean(s1, JSONConfig.create().setDateFormat(DatePattern.NORM_DATETIME_MINUTE_PATTERN), Message.class);


        JSONObject jsonObject = JSONUtil.parseObj(message);
        System.out.println(jsonObject);
        jsonObject.setDateFormat(DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        System.out.println(jsonObject);
    }
}
