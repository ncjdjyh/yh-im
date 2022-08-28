package com.neo.yhrpc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: neo
 * @FirstInitial: 2019/7/13
 * @Description: ~
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageOutput {
    private String requestId;
    private String type;
    private Object payload;
}
