package com.neo.im.presence;

import com.neo.im.common.HostAddress;
import com.neo.im.presence.entity.command.LoginCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@RestController
public class PresenceApiServer {
    @Autowired
    IClientStateService userStateService;

    @GetMapping("/getUserConnectHostAddress")
    public ResponseEntity<HostAddress> getUserConnectHostAddress(@RequestParam Long id) {
        HostAddress connectedServer = userStateService.getConnectedServer(id);
        return ResponseEntity.ok(connectedServer);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(Long id) {
        userStateService.activeState(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam Long id) {
        userStateService.inActiveState(id);
        return ResponseEntity.ok().build();
    }
}
