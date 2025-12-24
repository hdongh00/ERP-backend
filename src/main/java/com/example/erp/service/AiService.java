package com.example.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    /**
     * RAG 핵심 로직
     * 사용자 질문을 벡터로 변환해서 DB검색
     * 검색된 연관 데이터를 프롬프트에 적용
     * AI에게 최종 답변 요청
     */
    public String askAi(String question){
        //벡터 저장소에서 질문과 가장 비슷한 문서 3개 검색
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.query(question).withTopK(10) //상위 10개만
        );
        if(similarDocuments.isEmpty()){
            return "죄송해요, 관련 정보를 찾을 수 없습니다.";
        }
        // 검색된 문서들의 내용을 하나의 문자열로 합침
        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        //AI에 프롬프트 적용
        String prompt = """
                당신은 재고 관리 비서입니다.
                제공된 [자재 정보]의 '재고상태' 항목을 보고 답변하세요.
                
                [규칙]
                1. 사용자가 **'특정 자재'의 수량이나 상태를 콕 집어 물어본 경우**:
                   - 재고 상태(충분/부족)와 상관없이, 현재고와 안전재고 수치를 정확하게 말해주세요.
                   - 예: "로지텍 마우스는 현재 15개 있습니다. (안전재고 10개)"
                
                2. 사용자가 **'재고 부족한 거', '발주 필요한 거'를 전체적으로 물어본 경우**:
                   - '재고상태'가 "재고부족_발주필요!!"인 항목만 추려서 말해주세요.
                   - 부족한 게 없으면 "모든 자재의 재고가 충분합니다."라고 답하세요.
                
                3. 사용자가 등록되지 않은 상품에 대해 물어볼 시, 솔직하게 "등록되지 않은 상품입니다."라고 답변하세요.
                
                [자재 정보]
                %s
                
                [사용자 질문]
                %s
                """.formatted(context,question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
