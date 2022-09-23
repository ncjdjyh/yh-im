package com.neo.im.common.payload;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.neo.im.util.RequestId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author ncjdjyh
 * @since 2022/9/23
 */
@Data
public class GroupMessage {
    private String messageId;
    private Long userId;
    private String content;
    private Long channelId;
    @DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
    @JsonFormat(timezone = "GMT+8", pattern = DatePattern.NORM_DATETIME_PATTERN)
    private Date createTime;

    public GroupMessage(Long userId, String content, Long channelId) {
        this.messageId = RequestId.next();
        this.userId = userId;
        this.content = content;
        this.channelId = channelId;
        this.createTime = DateUtil.date();
    }
}
