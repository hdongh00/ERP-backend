package com.example.erp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

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
}
