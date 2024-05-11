package com.capstone.lawing.global.handler;

import com.capstone.lawing.global.dto.Response.ErrorResponseDTO;
import com.capstone.lawing.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<ErrorResponseDTO> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponseDTO.toResponseEntity(e.getErrorCode());
    }

}
