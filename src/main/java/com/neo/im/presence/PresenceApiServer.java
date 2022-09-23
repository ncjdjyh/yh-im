package com.neo.im.presence;

import com.neo.im.common.HostAddress;
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
    IUserStateService userStateService;

    @GetMapping("/getUserConnectHostAddress")
    public ResponseEntity<HostAddress> getUserConnectHostAddress(@RequestParam Long id) {
        HostAddress connectedServer = userStateService.getConnectedServer(id);
        return ResponseEntity.ok(connectedServer);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(Long id) {
        userStateService.login(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam Long id) {
        userStateService.logout(id);
        return ResponseEntity.ok().build();
    }
}
