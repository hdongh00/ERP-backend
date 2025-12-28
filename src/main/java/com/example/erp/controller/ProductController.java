package com.example.erp.controller;

import com.example.erp.entity.Product;
import com.example.erp.repository.ProductRepository;
import com.example.erp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping("/product/upload")
    public String uploadPage() {
        return "product/upload";
    }

    @PostMapping("/product/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try{
            productService.uploadExcel(file);
        }catch(Exception e){
            e.printStackTrace();
            return "redirect:/product/upload?error";
        }
        return "redirect:/product/list";
    }
    @GetMapping("/product/list")
    public String listPage(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        //페이지 설정
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "id"));

        //findAll을 호출하면 page 객체 소환
        Page<Product> paging = productRepository.findAll(pageable);
        //productList라는 이름을 붙여서 보여줌
        model.addAttribute("productList", paging);
        return "product/list";
    }
}
