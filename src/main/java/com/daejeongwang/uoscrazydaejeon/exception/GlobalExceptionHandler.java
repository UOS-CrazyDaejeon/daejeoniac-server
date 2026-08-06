package com.daejeongwang.uoscrazydaejeon.exception;

import com.daejeongwang.uoscrazydaejeon.dto.ResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 걍 일반적인 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultDto> handleAllExceptions(Exception e) {
        logger.error("An unexpected error occurred", e);

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("Internal server error")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resultDto);
    }

    // NotFound 예외
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResultDto> handleResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource Not Found : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("Resource not Found : " + e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(resultDto);
    }

    // 유효하지 않은 토큰 예외
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ResultDto> handleInvalidToken(InvalidTokenException e) {
        logger.error("Invalid Token : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("Invalid Token : " + e.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(resultDto);
    }

    // AuthenticationFailed
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ResultDto> handleAuthenticationFailed(AuthenticationFailedException e) {
        logger.error("AuthenticationFailed : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("AuthenticationFailed : " + e.getMessage())
                .code(HttpStatus.UNAUTHORIZED.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(resultDto);
    }

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultDto> handleIllegalArgument(IllegalArgumentException e) {
        logger.error("IllegalArgumentException : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("IllegalArgumentException : " + e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resultDto);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResultDto> handleIllegalState(IllegalStateException e) {
        logger.error("IllegalStateException : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("IllegalStateException : " + e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resultDto);
    }

    @ExceptionHandler(AppleApiException.class)
    public ResponseEntity<ResultDto> handleAppleApiException(AppleApiException e) {
        logger.error("AppleApiException : {}", e.getMessage());

        ResultDto resultDto = ResultDto.builder()
                .success(false)
                .message("AppleApiException : " + e.getMessage())
                .code(e.getStatus().value())
                .build();

        return ResponseEntity
                .status(e.getStatus())
                .body(resultDto);
    }

}
