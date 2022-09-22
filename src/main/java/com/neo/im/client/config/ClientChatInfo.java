package com.neo.im.client.config;

import cn.hutool.core.util.RandomUtil;
import com.neo.im.common.HostAddress;
import com.neo.im.common.RegisterCenter;
import lombok.Data;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@Data
public class ClientChatInfo {
   private static ClientChatInfo info;
   private Long clientId;
   private String username;
   private HostAddress chatHostAddress;
   private HostAddress presenceHostAddress;

   public static synchronized ClientChatInfo getInstance(Long clientId) {
      if (info == null) {
         ClientChatInfo clientChatInfo = new ClientChatInfo();
         clientChatInfo.setUsername(RandomUtil.randomString(8));
         clientChatInfo.setClientId(clientId);
         clientChatInfo.setChatHostAddress(RegisterCenter.getChatHostAddress());
         clientChatInfo.setPresenceHostAddress(RegisterCenter.getPresenceHostAddress());

         return clientChatInfo;
      }
      return info;
   }

   @Override
   public String toString() {
      return "ClientChatInfo{" +
              "clientId=" + clientId +
              ", username='" + username + '\'' +
              ", chatHostAddress=" + chatHostAddress +
              ", presenceHostAddress=" + presenceHostAddress +
              '}';
   }
}
