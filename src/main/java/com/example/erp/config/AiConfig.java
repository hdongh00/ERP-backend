package com.example.erp.config;

import com.example.erp.dto.*;
import com.example.erp.entity.Product;
import com.example.erp.repository.ProductRepository;
import com.example.erp.service.ApprovalService;
import com.example.erp.service.OrderService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
public class AiConfig {
    //M2 버전 방식
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder.build();
    }

    //기안 작성 도구
    @Bean
    @Description("사용자가 발주를 요청했을 때, 즉시 실행하지 않고 '결재 기안(Draft)'을 작성하는 기능")
    public Function<OrderDraftRequest, String> draftOrderFunction(ApprovalService approvalService){
        return request -> approvalService.createDraftByAi(request.productName(), request.quantity());
    }

    @Bean
    @Description("창고의 실제 재고(Real Stock)를 감소시킬 때만 사용하는 도구. 단순한 '기안 취소'나 '발주 취소'에는 절대 사용하지 마시오.")
    public Function<OrderCancelRequest, String> cancelOrderFunction(OrderService orderService){
        return request -> {
            return orderService.cancelOrder(request.productName(), request.quantity());
        };
    }

    //상품 정보를 기억하는 메서드
    @Bean
    ApplicationRunner initVectorData(ProductRepository productRepository, VectorStore vectorStore){
        return args -> {
            //DB에 있는 모든 상품 가져오기
            List<Product> allProducts = productRepository.findAll();
            List<Document> documents = new ArrayList<>();

            //상품을 AI용 설명서로 변환
            for(Product p : allProducts){
                String description = String.format("""
                        [상품 정보]
                        상품명: %s
                        가격: %d원
                        상태: %s
                        
                        이 제품은 우리 회사의 자재 관리 품목입니다.
                        재고 수량이나 발주가 필요하면 반드시 도구를 사용하여 확인해야 합니다.
                        """,
                        p.getName(),
                        p.getPrice(),
                        p.getDescription()
                );
                documents.add(new Document(description));
            }
            // 3. 회사 정책 문서 추가 (시뮬레이션 질문용)
            String policy = """
                    [우리 회사 안전재고 관리 정책]
                    1. 모든 전자제품류(램, 모니터, 마우스 등)는 최소 안전재고를 유지해야 합니다.
                    2. 재고가 부족할 경우, 평소 판매량의 1.5배 수준으로 넉넉하게 발주하는 것을 권장합니다.
                    3. 발주 전에는 반드시 현재 실재고를 확인해야 합니다.
                    """;
            documents.add(new Document(policy));

            // 4. 벡터 스토어에 저장
            vectorStore.add(documents);
            System.out.println("✅ AI 지식 데이터(RAG) 로딩 완료! (initVectorData 실행됨)");
        };
    }

    //재고 조회 도구
    @Bean
    @Description("특정 품목의 현재 재고 수량과 상태를 정확하게 조회하는 기능")
    public Function<ProductSearchRequest, String> searchProductFunction(OrderService orderService){
        return request -> orderService.getProductStatus(request.productName());
    }

    //전체 조회 도구
    @Bean
    @Description("사용자가 '전체 목록 보여줘', '품목 리스트 알려줘', '뭐 있어?' 같이 모든 상품을 궁금해할 때 사용하는 도구")
    public Function<ProductListRequest, String> listProductsFunction(OrderService orderService){
        return request -> orderService.getAllProductList();
    }
    //대화 내용 저장 메모리 저장소 생성
    @Bean
    public ChatMemory chatMemory(){
        return new InMemoryChatMemory();
    }
}
