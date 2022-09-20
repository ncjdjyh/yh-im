package com.neo.im.presence.entity.command;

import com.neo.im.common.HostAddress;
import lombok.Data;

/**
 * @author ncjdjyh
 * @since 2022/9/20
 */
@Data
public class LoginCommand {
    private Long id;
    private HostAddress hostAddress;
}
