package com.capstone.lawing.domain.license.dto.response;

import lombok.Getter;

@Getter
public class CodefTokenResponseDto {

    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;

}
