package com.capstone.lawing.domain.memberToken.Repository;

import com.capstone.lawing.domain.memberToken.MemberToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {

    Optional<MemberToken> findByMemberId(Long memberId);

    Optional<MemberToken> findByRefreshToken(String refreshToken);

}
