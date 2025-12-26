package com.example.erp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

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
                당신은 스마트한 자재 관리 AI 비서입니다.
                
                [행동 지침]
                1. 사용자가 제품의 **설명, 가격, 특징**이나 **회사 정책**을 물어보면 [자재 정보(RAG)]를 참고하여 답변하세요.
                2. 사용자가 **'재고 수량', '몇 개 남았어?', '상태 확인'** 등을 물어보면, 당신의 기억을 믿지 말고 무조건 **'조회 도구(searchProductFunction)'를 실행**하세요.
                3. [자재 정보]에는 실시간 재고 수량이 없습니다. 모른다고 대답하지 말고 도구를 사용해서 알아내세요.
                4. 사용자가 **"전체 목록", "모든 품목", "리스트"**를 요청하면,
                   RAG(기억)를 뒤지지 말고 **'전체 목록 도구(listProductsFunction)'**를 사용해서 정확한 리스트를 가져오세요.
                   (이 도구는 품목명, 가격, 재고 수량, 상태를 모두 제공합니다.)
                
                [매우 중요]
                사용자가 **"발주해줘", "주문해줘", "사줘"**라고 요청하면,
                절대로 '즉시 발주(placeOrderFunction)'를 사용하지 마십시오.
                대신 무조건 **'기안 작성 도구(draftOrderFunction)'를 사용**하여 결재를 먼저 올리십시오.
                ("즉시 처리해"라고 명시할 때만 placeOrderFunction 사용)
                사용자가 **"방금 그거 취소해", "발주 취소해"**라고 요청할 경우:
                - 아직 관리자 승인이 안 난 '결재 대기' 상태라면, **어떠한 도구도 실행하지 마십시오.**
                - 대신 **"결재 대기 중인 건은 승인 페이지에서 '반려' 처리를 해주셔야 합니다."**라고 안내하세요.
                - '재고 감소 도구(cancelOrderFunction)'는 오직 **'이미 입고된 물건을 반품하거나 폐기할 때'**만 사용하세요.
                
                [자재 정보]
                %s
                
                [사용자 질문]
                %s
                """.formatted(context,question);

        return chatClient.prompt()
                .user(prompt)
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "default")
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .functions( "cancelOrderFunction", "searchProductFunction", "draftOrderFunction", "listProductsFunction")
                .call()
                .content();
    }
}
