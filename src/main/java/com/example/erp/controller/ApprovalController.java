package com.example.erp.controller;

import com.example.erp.entity.Approval;
import com.example.erp.service.ApprovalService;
import com.example.erp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ApprovalController {
    private final ApprovalService approvalService;
    private final ProductService productService;

    //결재 요청 화면 보여주기
    @GetMapping("/approval/new")
    public String createForm(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "approval/form";
    }

    //결재 요청 처리
    @PostMapping("/approval/new")
    public String createApproval(@RequestParam("productId") Long productId,
                                 @RequestParam("quantity") int quantity,
                                 @RequestParam("requesterName") String requesterName) {
        approvalService.createApproval(productId, quantity, requesterName);
        return "redirect:/approval/list"; //저장 후 목록으로 이동
    }

    //결재 목록 보기 (팀장 뷰)
    @GetMapping("/approval/list")
    public String list(Model model) {
        model.addAttribute("approvals", approvalService.getAllApprovals());
        return "approval/list";
    }

    //승인 처리
    @PostMapping("/approval/{id}/approve")
    public String approve(@PathVariable("id") Long id){
        approvalService.approve(id);
        return "redirect:/approval/list";
    }

    //반려 처리
    @PostMapping("/approval/{id}/reject")
    public String reject(@PathVariable("id") Long id){
        approvalService.reject(id);
        return "redirect:/approval/list";
    }
}
