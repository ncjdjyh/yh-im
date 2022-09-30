package com.neo.im.chat.entity;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author ncjdjyh
 * @since 2022/9/30
 */
@Data
public class SendGroupMessageCommand {
    private String messageId;
    private Long userId;
    private String content;
    private Long channelId;
    private Long messageTo;
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_PATTERN)
    private Date createTime;
}
