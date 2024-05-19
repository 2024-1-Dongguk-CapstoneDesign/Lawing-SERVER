package com.capstone.lawing.domain.license;

import com.capstone.lawing.domain.BaseTimeEntity;
import com.capstone.lawing.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Builder
@AllArgsConstructor
public class License extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "license_id")
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "license_number", nullable = false)
    private String licenseNumber;

    @Column(name = "license_birth", nullable = false)
    private String licenseBirth;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

}
