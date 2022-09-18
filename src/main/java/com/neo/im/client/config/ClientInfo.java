package com.neo.im.client.config;

import cn.hutool.core.util.RandomUtil;
import lombok.Data;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Data
public class ClientInfo {
   private static ClientInfo info;
   private Long clientId;
   private String username;

   public static synchronized ClientInfo getInstance() {
      if (info == null) {
         ClientInfo clientInfo = new ClientInfo();
         clientInfo.setUsername(RandomUtil.randomString(8));
         clientInfo.setClientId(RandomUtil.randomLong(1, 50));
         return clientInfo;
      }
      return info;
   }
}
