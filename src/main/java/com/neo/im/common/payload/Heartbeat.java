package com.neo.im.common.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Data
@AllArgsConstructor
public class Heartbeat {
   private Long clientId;
   private Object content;
}
