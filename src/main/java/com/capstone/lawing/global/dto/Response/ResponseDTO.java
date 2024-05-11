package com.capstone.lawing.global.dto.Response;

import com.capstone.lawing.global.enumType.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
@Getter
@Builder
@RequiredArgsConstructor
@JsonIgnoreProperties
public class ResponseDTO {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String status;
    private final String description;
    private final String message;

    public static ResponseEntity<ResponseDTO> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseDTO.builder()
                        .statusCode(errorCode.getHttpStatus().value())
                        .status(errorCode.getHttpStatus().name())
                        .description(errorCode.name())
                        .message(errorCode.getMessage())
                        .build()
                );
    }

}
