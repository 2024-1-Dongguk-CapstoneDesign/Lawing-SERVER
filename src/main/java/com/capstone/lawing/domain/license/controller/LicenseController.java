package com.capstone.lawing.domain.license.controller;

import com.capstone.lawing.domain.license.dto.LicenseValidDTO;
import com.capstone.lawing.domain.license.dto.response.CodefTokenResponseDto;
import com.capstone.lawing.domain.license.service.LicenseService;
import com.capstone.lawing.global.auth.adapter.MemberAdapter;
import com.capstone.lawing.global.dto.Response.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "LicenseController" , description = "운전면허증 유효성 검증 API")
public class LicenseController {

    private final LicenseService licenseService;

    /**
     * 운전면허증 OCR
     * @return ResponseDTO
     */
    @Operation(summary = "운전면허증 OCR API", description = "사용자 운전면허증의 텍스트를 인식합니다.")
    @PostMapping(value = "/license")
    public ResponseDTO licenseOCR(@RequestParam MultipartFile multipartFile , @AuthenticationPrincipal MemberAdapter memberAdapter) throws Exception {

        return licenseService.getLicenseOCR(multipartFile,memberAdapter.getMember());

    }

    /**
     * 운전면허증 유효성 검증
     * @param licenseValidDTO 운전 면허증 정보
     * @return ResponseDTO
     */
    @Operation(summary = "운전면허증 유효성 검증 API", description = "사용자 운전면허증의 유효성을 검증합니다.")
    @PostMapping("/license/valid")
    public ResponseDTO licenseValid(@RequestBody LicenseValidDTO licenseValidDTO , @AuthenticationPrincipal MemberAdapter memberAdapter){

        return licenseService.getlicenseValid(licenseValidDTO , memberAdapter.getMember());

    }

    /**
     * codef Token 발급
     * @return CodefTokenResponseDto
     */
    @Operation(summary = "Codef 토큰 발급 API", description = "codef Access Token을 발급 받습니다.")
    @GetMapping("/license/token")
    public CodefTokenResponseDto licenseToken() {

        return licenseService.getLicenseToken();

    }

}

