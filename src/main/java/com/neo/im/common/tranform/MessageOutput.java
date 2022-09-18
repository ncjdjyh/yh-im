package com.neo.im.common.tranform;

import com.neo.im.util.RequestId;
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
public class MessageOutput {
    private String requestId;
    private Integer type;
    private Object payload;

    public MessageOutput(Integer type, Object payload) {
        this.requestId = RequestId.next();
        this.type = type;
        this.payload = payload;
    }
}
