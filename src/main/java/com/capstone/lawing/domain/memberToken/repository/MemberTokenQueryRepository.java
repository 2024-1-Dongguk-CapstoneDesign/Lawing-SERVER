package com.capstone.lawing.domain.memberToken.repository;

public interface MemberTokenQueryRepository {
    String findEmailByRefreshToken(String refreshToken);

}
