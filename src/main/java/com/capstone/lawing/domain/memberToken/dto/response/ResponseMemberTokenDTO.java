package com.capstone.lawing.domain.memberToken.dto.response;

import lombok.Data;
import lombok.Getter;
@Getter
@Data
public class ResponseMemberTokenDTO {

    private String accessToken;
    private String refreshToken;

    public ResponseMemberTokenDTO(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
