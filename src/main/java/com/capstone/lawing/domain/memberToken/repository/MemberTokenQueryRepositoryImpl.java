package com.capstone.lawing.domain.memberToken.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import static com.capstone.lawing.domain.memberToken.QMemberToken.memberToken;

@Repository
public class MemberTokenQueryRepositoryImpl implements MemberTokenQueryRepository{

    private final JPAQueryFactory query;

    public MemberTokenQueryRepositoryImpl(EntityManager em){
        this.query = new JPAQueryFactory(em);
    }

    /**
     * 리프레시 토큰으로 회원 이메일 조회
     */
    @Override
    public String findEmailByRefreshToken(String refreshToken) {
        return query
                .select(memberToken.member.email)
                .from(memberToken)
                .where(memberToken.refreshToken.eq(refreshToken))
                .fetchOne();
    }

}
