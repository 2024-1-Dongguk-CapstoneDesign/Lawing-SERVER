package com.capstone.lawing.domain.memberToken;

import com.capstone.lawing.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Builder
@AllArgsConstructor
public class MemberToken{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_token_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "access_token", nullable = false)
    @NotNull
    private String accessToken;

    @Column(name = "access_expiration", nullable = false)
    private LocalDateTime accessExpiration;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "refresh_expiration", nullable = false)
    private LocalDateTime refreshExpiration;

    public void updateTokens(String newAccessToken, LocalDateTime newAccessExpiration, String newRefreshToken, LocalDateTime newRefreshExpiration) {
        this.accessToken = newAccessToken;
        this.accessExpiration = newAccessExpiration;
        this.refreshToken = newRefreshToken;
        this.refreshExpiration = newRefreshExpiration;
    }

}
