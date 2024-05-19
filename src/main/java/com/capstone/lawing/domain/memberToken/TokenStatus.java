package com.capstone.lawing.domain.memberToken;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenStatus {
    VALID("유효"), EXPIRED("만료");

    public String getName() {
        return name;
    }

    private String name;
}
