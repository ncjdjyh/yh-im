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
public class SendMessageCommand {
    private String messageId;
    private Long messageFrom;
    private Long messageTo;
    private String content;
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_PATTERN)
    private Date createTime;
}
