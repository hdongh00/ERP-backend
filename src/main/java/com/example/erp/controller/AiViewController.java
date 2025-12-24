package com.example.erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AiViewController {
    @GetMapping("/ai/chat")
    public String chatPage(){
        return "ai/chat";
    }
}
