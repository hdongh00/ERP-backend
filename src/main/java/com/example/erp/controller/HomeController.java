package com.example.erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // "http://localhost:8080/" 으로 들어오면
    @GetMapping("/")
    public String home() {
        // "/products" (재고 목록) 페이지로 이동
        return "redirect:/product/list";
    }
}