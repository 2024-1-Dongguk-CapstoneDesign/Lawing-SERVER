package com.capstone.lawing.domain.member.controller;

import com.capstone.lawing.domain.member.dto.request.RequestLoginTypeDTO;
import com.capstone.lawing.domain.member.service.MemberService;
import com.capstone.lawing.domain.memberToken.dto.response.ResponseMemberTokenDTO;
import com.capstone.lawing.global.dto.Response.ResponseDTO;
import com.capstone.lawing.global.enumType.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
@Tag(name = "MemberController" , description = "회원 API")
public class MemberController {

    private final MemberService memberService;

    /**
     * 소셜 로그인 (카카오)
     */
    @Operation(summary = "카카오 로그인" , description = "사용자의 카카오 계정을 통해 로그인을 시도합니다.")
    @PostMapping("/social/login")
    public ResponseMemberTokenDTO socialLogin(@RequestBody RequestLoginTypeDTO requestLoginTypeDTO, @RequestHeader("kakaoAccessToken") String kakaoAccessToken ){

        return memberService.doSocialLogin(requestLoginTypeDTO.getSocialType(), kakaoAccessToken);

    }
}
