package com.example.erp.service;

import com.example.erp.entity.Product;
import com.example.erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final ProductRepository productRepository;
    private final VectorStore vectorStore;

    /**
     * DB의 모든 상품 정보를 읽어서 벡터 저장소에 동기화하는 메서드
     */
    @Transactional
    public void syncProductData(){
        //DB에서 모든 상품 가져오기
        List<Product> products = productRepository.findAll();
        List<Document> documents = new ArrayList<>();

        for(Product product:products){
            String statusResult;
            if(product.getStockQuantity() < product.getSafetyStock()){
                statusResult = "재고부족_발주필요!";
            }else{
                statusResult = "재고충분_문제없음";
            }

            //AI에게 학습시킬 문장 만들기
            String content = String.format("상품명: %s, 가격: %d원, 설명: %s, 현재고: %d개, 안전재고: %d개, 상태: %s",
                    product.getName(),
                    product.getPrice(),
                    product.getDescription(),
                    product.getStockQuantity(),
                    product.getSafetyStock(),
                    statusResult
            );

            //메타데이터 만들기
            Map<String, Object> metadata = Map.of(
                    "productId", product.getId(),
                    "category", "ELECTRONICS"
            );

            //Spring AI 전용 "Document"객체로 변환
            String uniqueId = UUID.nameUUIDFromBytes(String.valueOf(product.getId()).getBytes()).toString();
            documents.add(new Document(uniqueId, content, metadata));
        }
        //벡터 저장소에 저장
        if(!documents.isEmpty()){
            vectorStore.add(documents);
            System.out.println("✅ 상품 데이터 " + documents.size() + "건이 벡터 저장소에 등록되었습니다.");
        }
    }
}
