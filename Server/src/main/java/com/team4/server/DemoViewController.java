package com.team4.server;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoViewController {
	@RequestMapping("/")
    public String home(Model model) {
    	System.out.println("Hello called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        model.addAttribute("title", "Team4 Demo");
        model.addAttribute("time", new Date().toString());
        return "home";
    }
}
