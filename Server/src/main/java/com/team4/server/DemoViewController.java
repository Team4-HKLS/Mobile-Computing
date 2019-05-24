package com.team4.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoViewController {
	@RequestMapping("/")
    public String index(Model model) {
        TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(time);

        model.addAttribute("title", "Team4 Demo");
        model.addAttribute("time", df.format(date));
        model.addAttribute("list",App.List);
        model.addAttribute("state",App.state);
        return "home";
    }
}
