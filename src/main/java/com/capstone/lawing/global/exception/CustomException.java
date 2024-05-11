package com.capstone.lawing.global.exception;

import com.capstone.lawing.global.enumType.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {

    private ErrorCode errorCode;

}
