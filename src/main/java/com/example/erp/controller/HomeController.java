package com.example.erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 1. "http://localhost:8080/" 으로 들어오면
    @GetMapping("/")
    public String home() {
        // 2. "/products" (재고 목록) 페이지로 토스!
        // (만약 상품 목록 주소가 다른 거라면 그 주소로 바꾸세요)
        return "redirect:/product/list";
    }
}