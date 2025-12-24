package com.example.erp.controller;

import com.example.erp.service.AiService;
import com.example.erp.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController //글자만 돌려주는 기능
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final EmbeddingService embeddingService;
    private final AiService aiService;

    @GetMapping("/sync")
    public String syncVectors(){
        embeddingService.syncProductData();
        return "✅ AI 데이터 동기화 완료! (벡터 DB 저장 성공)";
    }

    @GetMapping("/ask")
    public String ask(@RequestParam("q") String question){
        return aiService.askAi(question);
    }
}
