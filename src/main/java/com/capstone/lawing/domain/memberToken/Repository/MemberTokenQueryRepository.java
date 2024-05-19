package com.capstone.lawing.domain.memberToken.Repository;

public interface MemberTokenQueryRepository {
    String findEmailByRefreshToken(String refreshToken);

}
