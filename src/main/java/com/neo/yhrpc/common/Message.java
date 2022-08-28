package com.neo.yhrpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
@Data
public class Message {
    String messageId;
    Long messageFrom;
    Long messageTo;
    String content;
    LocalDateTime createTime;

    public Message(Long messageFrom, Long messageTo, String content) {
        this.messageId = RequestId.next();
        this.messageFrom = messageFrom;
        this.messageTo = messageTo;
        this.content = content;
        this.createTime = LocalDateTime.now();
    }
}
