package com.capstone.lawing.global.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    FAILURE(BAD_REQUEST, "FAILURE");

    private final HttpStatus httpStatus;
    private final String message;


}
