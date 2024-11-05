package org.myweb.first.common.exception;

import org.myweb.first.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 모든 예외들을 핸들
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllExceptions(Exception e){
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("서버 오류가 발생했습니다.")
                .data(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Handle specific exceptions as needed
    // @ExceptionHandler(SpecificException.class)
    // public ResponseEntity<ApiResponse<String>> handleSpecificException(SpecificException ex) {
    //     // Custom response
    // }
}
