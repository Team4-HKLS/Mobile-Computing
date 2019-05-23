package com.team4.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping(value = "/reset")
public class ResetAllController {
    @GetMapping
    public String reset() {
    	App.resetAll();
        return "redirect:/";
    }
}
