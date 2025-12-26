package com.example.erp.service;

import com.example.erp.entity.Product;
import com.example.erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;

    //AI가 재고를 물어볼 때 사용하는 조회 메서드
    public String getProductStatus(String productName){
        log.info("AI가 재고 조회함: {}", productName);

        Optional<Product> productOpt = productRepository.findByName(productName);
        if(productOpt.isEmpty()){
            return "정보 없음: '" + productName + "'라는 제품을 찾을 수 없습니다.";
        }
        Product product = productOpt.get();
        //AI가 읽고 바로 대답할 수 있도록 리턴
        return String.format("확인 결과, '%s'의 현재 재고는 %d개입니다. (안전재고: %d개, 상태: %s)",
                product.getName(),
                product.getStockQuantity(),
                product.getSafetyStock(),
                (product.getStockQuantity() < product.getSafetyStock()) ? "재고 부족" : "정상");
    }

    @Transactional
    public String placeOrder(String productName, int quantity) {
        log.info("AI가 발주 요청함: {} / {}개",  productName, quantity);

        //상품찾기
        Optional<Product> productOpt = productRepository.findByName(productName);
        if(productOpt.isEmpty()){
            return "실패: '" + productName + "'라는 상품을 찾을 수 없습니다. 정확한 품목명을 말씀해주세요.";
        }

        //재고 증가(입고 처리)
        Product product = productOpt.get();
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);

        return "성공: " + productName + " " + quantity + "개 발주가 완료되었습니다. (현재고: " + product.getStockQuantity() + "개)";
    }

    @Transactional
    public String cancelOrder(String productName, int quantity) {
        log.info("AI가 발주 취소 요청함: {} / {}개",  productName, quantity);

        Optional<Product> productOpt = productRepository.findByName(productName);
        if(productOpt.isEmpty()){
            return "실패: '" + productName + "' 상품을 찾을 수 없습니다.";
        }

        Product product = productOpt.get();

        //유효성 검사: 현재 재고보다 더 많이 취소X
        if(product.getStockQuantity() < quantity){
            return "실패: 취소하려는 수량이 현재 재고보다 많습니다. (현재: " + product.getStockQuantity() + "개)";
        }

        //재고 감소(원상 복구)
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        return String.format(
                "처리 완료: %s %d개 발주 취소 처리가 DB에 반영되었습니다. (시스템 상의 실제 현재고: %d개). 이 수치를 사용자에게 그대로 전달하세요.",
                productName, quantity, product.getStockQuantity()
        );
    }
    //전체 조회 도구
    @Transactional(readOnly = true)
    public String getAllProductList(){
        List<Product> products = productRepository.findAll();

        if(products.isEmpty()){
            return "현재 등록된 품목이 없습니다.";
        }
        StringBuilder sb = new StringBuilder("전체 자재 현황 리스트입니다:\n");
        for(Product p : products){
            sb.append(String.format("- %s : 현재고 %d개 (안전재고 %d개, 상태: %s, 가격: %d원)\n",
                    p.getName(),
                    p.getStockQuantity(),
                    p.getSafetyStock(),
                    p.getStatus(),
                    p.getPrice()));
        }
        return sb.toString();
    }
}
