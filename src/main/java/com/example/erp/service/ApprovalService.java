package com.example.erp.service;

import com.example.erp.entity.Approval;
import com.example.erp.entity.ApprovalStatus;
import com.example.erp.entity.Product;
import com.example.erp.repository.ApprovalRepository;
import com.example.erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true) //기본적으로 조회만 함
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final ProductRepository productRepository;

    /**
     * 결제 요청하기
     * 직원이 이 제품 사달라고 하고 저장하는 단계
     */
    @Transactional
    public void createApproval(Long productId, int quantity, String requesterName){
        //상품 조회 (없으면 에러)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        //결제 문서 생성
        Approval approval = Approval.builder()
                .product(product)
                .quantity(quantity)
                .requesterName(requesterName)
                .build(); // status는 자동으로 대기로 들어감
        //저장
        approvalRepository.save(approval);
    }
    /**
     * 결재 목록 조회
     * 팀장이 볼 리스트
     * N+1 문제 해결
     */
    public List<Approval> getAllApprovals(){
        //return approvalRepository.findAll();
        return approvalRepository.findAllWithProduct();
    }
    /**
     * 결재 승인
     * 팀장이 승인 버튼 누르면 -> 결재 상태 변경 AND 재고 증가
     */
    @Transactional
    public void approve(Long approvalId){
        //결재 건 조회
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("결재 건이 없습니다."));

        // 이미 처리된 건인지 검증
        if(approval.getStatus() != ApprovalStatus.WAIT){
            throw new IllegalArgumentException("이미 처리된 결재입니다.");
        }
        //결재 상태 변경
        approval.approve();

        //상품 재고 실제로 증가시키기
        approval.getProduct().addStock(approval.getQuantity());
    }
    /**
     * 결재 반려
     * 그냥 상태만 반려로 바꿈
     */
    @Transactional
    public void reject(Long approvalId){
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new IllegalArgumentException("결재 건이 없습니다."));

        if(approval.getStatus() != ApprovalStatus.WAIT){
            throw new IllegalArgumentException("이미 처리된 결재입니다.");
        }
        approval.reject();
    }

    //이름으로 찾아서 기안 올리기
    @Transactional
    public String createDraftByAi(String productName, int quantity){
        //이름으로 상품 찾기
        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productName));

        //현재 로그인한 사용자의 아이디 가져옴
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();

        createApproval(product.getId(), quantity, currentUserName);
        return "결재 기안 등록 완료: " + productName + " " + quantity + "개 (상태: 대기중)";
    }
}
