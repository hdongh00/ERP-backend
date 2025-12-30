package com.example.erp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@Slf4j //로그 남기기 위해 사용
@ControllerAdvice // 컨트롤러에서 발생하는 에러를 다 잡겠다는 기능
public class GlobalExceptionHandler {
    /**
     * 모든 예외를 처리하는 메서드
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllException(Exception e, Model model) {
        //서버 로그에 에러 내용 기록
        log.error("🚨 시스템 에러 발생: ", e);

        //화면으로 보낼 에러 메시지 담음
        //테스트 용이라 e.getMessage()를 보냄
        model.addAttribute("errorMessage", e.getMessage());

        return "error/global-error";
    }
    /**
     * 동시성 충돌 발생 시 처리
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public String handleConflict(ObjectOptimisticLockingFailureException e, Model model) {
        log.error("🚨 동시성 이슈 발생: ", e);

        model.addAttribute("errorMessage",
                "죄송합니다. 다른 관리자가 방금 이 정보를 수정했습니다. \n" +
                "데이터 정합성을 위해 목록으로 돌아가서 다시 확인해주세요.");

        return "error/global-error";
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleResourceNotFound(NoResourceFoundException e) {
        return null; // 아무것도 반환하지 않으면 스프링이 알아서 조용히 처리함
    }
}
