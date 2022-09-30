package com.neo.im.common.payload;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.im.util.RequestId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: ncjdjyh
 * @since: 2022/8/28
 */
@Data
@NoArgsConstructor
public class Message {
    private String messageId;
    private Long messageFrom;
    private Long messageTo;
    private String content;
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_PATTERN)
    private Date createTime;

    public Message(Long messageFrom, Long messageTo, String content) {
        this.messageId = RequestId.next();
        this.messageFrom = messageFrom;
        this.messageTo = messageTo;
        this.content = content;
        this.createTime = DateUtil.date();
    }
}
