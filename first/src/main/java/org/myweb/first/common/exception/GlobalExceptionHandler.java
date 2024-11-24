package org.myweb.first.common.exception;

import org.myweb.first.common.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // @Valid 어노테이션을 통해 유효성 검사할 때 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors=new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName= ((FieldError) error).getField(); // 필드 이름
            String errorMessage=error.getDefaultMessage(); // 오류 메시지
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("유효성 검사 오류입니다.")
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    }


    // 데이터 무결성 예외 처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(false)
                .message("데이터 무결성 위반: " + ex.getMostSpecificCause().getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict 반환

    }
    // 유효성 검사 예외 처리
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
