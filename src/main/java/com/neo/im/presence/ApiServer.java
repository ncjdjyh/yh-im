package com.neo.im.presence;

import com.neo.im.common.HostAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ncjdjyh
 * @since 2022/9/18
 */
@RestController
public class ApiServer {
    @Autowired
    IClientStateService userStateService;

    @GetMapping("/getUserConnectHostAddress")
    public ResponseEntity<HostAddress> getUserConnectHostAddress(@RequestParam Long id) {
        HostAddress connectedServer = userStateService.getConnectedServer(id);
        return ResponseEntity.ok(connectedServer);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam Long id, @RequestParam HostAddress hostAddress) {
        userStateService.activeState(id, hostAddress);
        return ResponseEntity.ok().build();
    }
}
