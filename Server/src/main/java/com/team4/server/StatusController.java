package com.team4.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ping")
public class StatusController {
    @GetMapping
    public String isRunning() {
        return "I'm Alive!";
    }
}
