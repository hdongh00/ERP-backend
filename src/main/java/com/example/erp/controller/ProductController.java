package com.example.erp.controller;

import com.example.erp.entity.Product;
import com.example.erp.service.ProductService;
import lombok.RequiredArgsConstructor;
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
    public String listPage(Model model) {
        List<Product> list = productService.getAllProducts();

        //productList라는 이름을 붙여서 보여줌
        model.addAttribute("productList", list);
        return "product/list";
    }

}
