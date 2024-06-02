package com.capstone.lawing.global.enumType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //면허증
    LICENSE_OCR_FAILED(BAD_REQUEST, "운전면허증 텍스트 추출을 실패하였습니다."),
    DRIVERS_LICENSE_EXISTS(BAD_REQUEST, "이미 등록된 운전면허증입니다."),
    LICENSE_VALIDATION_FAILED(BAD_REQUEST, "유효하지 않는 운전면허증입니다."),
    LICENSE_MEMBER_INFO_NOT_CORRECT(BAD_REQUEST,"로그인 회원 정보와 면허증 회원 정보가 일치하지 않습니다. "),
    WRONG_LICENSE_GENDER(BAD_REQUEST,"잘못된 성별 정보입니다."),

    //토큰 및 회원관련
    MEMBER_NOT_FOUND(BAD_REQUEST, "해당 회원이 존재하지 않습니다."),

    UNSUPPORTED_SOCIAL_TYPE(BAD_REQUEST,"지원하지 않는 소셜 로그인입니다."),
    KAKAO_RESPONSE_FAILED(INTERNAL_SERVER_ERROR,"카카오 서버로부터 응답이 원활하지 않습니다."),
    KAKAO_JOIN_FAILED(BAD_REQUEST,"카카오 계정을 통한 회원가입에 실패하였습니다."),
    KAKAO_LOGIN_FAILED(BAD_REQUEST,"카카오 계정을 통한 로그인에 실패하였습니다."),
    KAKAO_LOGIN_SUCCESS(OK,"카카오 계정을 통한 로그인이 완료되었습니다"),

    REFRESH_TOKEN_MISSING(BAD_REQUEST,"리프레시 토큰이 존재하지 않습니다,"),
    ACCESS_TOKEN_MISSING(BAD_REQUEST,"액세스 토큰이 존재하지 않습니다,"),
    MEMBER_TOKEN_NOT_FOUND(BAD_REQUEST,"해당 회원의 토큰이 존재하지 않습니다,"),
    TOKEN_EXPIRED(UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다.");


    public final HttpStatus httpStatus;
    public final String message;

}
