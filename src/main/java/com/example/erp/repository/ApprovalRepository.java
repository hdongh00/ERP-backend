package com.example.erp.repository;

import com.example.erp.entity.Approval;
import com.example.erp.entity.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval,Long> {
    //Approval을 가져올 때 Product도 같이 묶어서 가져옴
    @Query("SELECT a FROM Approval a JOIN FETCH a.product")
    List<Approval> findAllWithProduct();
    //특정 상태인 결재 건만 조회 + 상품 정보까지 한 번에 로딩(N+1 문제 방지)
    @Query("SELECT a FROM Approval a JOIN FETCH a.product WHERE a.status = :status")
    List<Approval> findByStatus(@Param("status") ApprovalStatus status);
}
