package com.capstone.lawing.domain.member.repository;

import com.capstone.lawing.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByBirthAndNameAndGender(String birth, String name, String gender);

}
