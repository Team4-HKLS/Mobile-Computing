package com.team4.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/next-stage")
public class NextStageController {
    @GetMapping
    public String NextStage() {
        App.moveNextStage();
        return "redirect:/";
    }
}
