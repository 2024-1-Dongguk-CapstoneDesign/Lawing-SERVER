package com.capstone.lawing.global.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    LICENSE_OCR_FAILED(BAD_REQUEST, "운전면허증 텍스트 추출을 실패하였습니다."),
    LICENSE_VALIDATION_FAILED(BAD_REQUEST, "운전면허증 텍스트 추출을 실패하였습니다.");


    public final HttpStatus httpStatus;
    public final String message;

}
