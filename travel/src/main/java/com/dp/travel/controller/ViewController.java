package com.dp.travel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/view")
    public String index() {
        return "index"; // index.html
    }
}