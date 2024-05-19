package com.capstone.lawing.domain.member.dto.request;

import com.capstone.lawing.domain.member.Member;
import com.capstone.lawing.domain.memberToken.MemberToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestTokenInfoDTO {

    private String accessToken;
    private LocalDateTime accessTokenExpiry;
    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;

    public MemberToken tokenInfoDTO(Member member, RequestTokenInfoDTO requestTokenInfoDTO){

        return MemberToken.builder()
                .member(member)
                .accessToken(requestTokenInfoDTO.getAccessToken())
                .accessExpiration(requestTokenInfoDTO.getAccessTokenExpiry())
                .refreshToken(requestTokenInfoDTO.getRefreshToken())
                .refreshExpiration(requestTokenInfoDTO.refreshTokenExpiry)
                .build();

    }
}
