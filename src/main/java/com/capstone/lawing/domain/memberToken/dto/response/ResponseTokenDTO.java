package com.capstone.lawing.domain.memberToken.dto.response;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Data
public class ResponseTokenDTO {

    private String token;
    private LocalDateTime expiryDate;

    public ResponseTokenDTO(String token, LocalDateTime expiryDate) {

        this.token=token;
        this.expiryDate=expiryDate;

    }
}
