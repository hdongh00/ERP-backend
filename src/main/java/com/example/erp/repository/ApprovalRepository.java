package com.example.erp.repository;

import com.example.erp.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval,Long> {
    //Approval을 가져올 때 Product도 같이 묶어서 가져옴
    @Query("SELECT a FROM Approval a JOIN FETCH a.product")
    List<Approval> findAllWithProduct();
}
