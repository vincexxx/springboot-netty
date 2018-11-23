package com.vince.server.monitor;

import com.vince.server.mapping.MessageChannelMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @RequestMapping("/dashboard")
    public String dashboard() {
        return MessageChannelMapping.INSTANCE.toString();
    }
}