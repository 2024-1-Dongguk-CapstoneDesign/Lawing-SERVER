package com.capstone.lawing.domain.memberToken.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/memberToken")
@Tag(name = "MemberTokenController" , description = "회원 토큰 API")
public class MemberTokenController {

    @Operation(summary = "JWT 재발급", description = "token을 재발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<HttpStatus> refreshToken() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
