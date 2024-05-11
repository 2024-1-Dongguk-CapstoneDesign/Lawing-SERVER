package com.capstone.lawing.domain.memberToken;

import com.capstone.lawing.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Builder
@AllArgsConstructor
public class MemberToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberTokenId")
    private Long id;

    @OneToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @Column(name = "accessToken")
    @NotNull
    private String accessToken;

    @Column(name = "accessExpiration")
    private LocalDateTime accessExpiration;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_expiration")
    private LocalDateTime refreshExpiration;

}
