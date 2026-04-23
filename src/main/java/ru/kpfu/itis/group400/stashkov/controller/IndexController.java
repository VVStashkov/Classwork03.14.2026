package ru.kpfu.itis.group400.stashkov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.kpfu.itis.group400.stashkov.aop.Loggable;

@Controller
public class IndexController {

    @Loggable
    @GetMapping(value = "index")
    public String index() {
        return "index";
    }
}