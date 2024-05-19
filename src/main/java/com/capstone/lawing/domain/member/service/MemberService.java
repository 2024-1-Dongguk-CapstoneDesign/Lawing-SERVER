package com.capstone.lawing.domain.member.service;

import com.capstone.lawing.domain.member.component.KakaoSocialLogin;
import com.capstone.lawing.global.dto.Response.ResponseDTO;
import com.capstone.lawing.global.enumType.ErrorCode;
import com.capstone.lawing.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final KakaoSocialLogin kakaoSocialLogin;

    /**
     * 소셜 로그인
     *
     * @param type             소셜 로그인 타입
     * @param kakaoAccessToken 소셜 로그인 액세스 토큰
     */
    public ResponseEntity<ResponseDTO> doSocialLogin(String type, String kakaoAccessToken){

        type = type.toLowerCase();

        try {
            if(type.equals("kakao")){
                kakaoSocialLogin.doKakaoLogin(kakaoAccessToken);
            }
        }catch (Exception e){
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }

        return ResponseDTO.toResponseEntity(ErrorCode.KAKAO_LOGIN_SUCCESS);

    }
}
