package com.capstone.lawing.domain.license;

import com.capstone.lawing.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Builder
@AllArgsConstructor
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "licenseId")
    private Long id;

    @OneToOne
    @JoinColumn(name = "memberId")
    private Member member;

    @Column(name = "ownerName")
    @NotNull
    private String ownerName;

    @Column(name = "licenseNumber")
    @NotNull
    private String licenseNumber;

    @Column(name = "licenseBirth")
    @NotNull
    private String licenseBirth;

    @Column(name = "serialNumber")
    @NotNull
    private String serialNumber;

}
