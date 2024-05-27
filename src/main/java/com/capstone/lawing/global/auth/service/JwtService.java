package com.capstone.lawing.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.capstone.lawing.domain.member.Member;
import com.capstone.lawing.domain.member.repository.MemberRepository;
import com.capstone.lawing.domain.memberToken.TokenStatus;
import com.capstone.lawing.domain.memberToken.dto.response.ResponseMemberTokenDTO;
import com.capstone.lawing.domain.memberToken.MemberToken;
import com.capstone.lawing.domain.memberToken.repository.MemberTokenQueryRepository;
import com.capstone.lawing.domain.memberToken.repository.MemberTokenRepository;
import com.capstone.lawing.domain.memberToken.dto.response.ResponseTokenDTO;
import com.capstone.lawing.global.auth.adapter.MemberAdapter;
import com.capstone.lawing.global.enumType.ErrorCode;
import com.capstone.lawing.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.NoSuchElementException;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN = "AccessToken";
    private static final String REFRESH_TOKEN = "RefreshToken";
    private static final String USERID_CLAIM = "account_email";
    private static final String BEARER = "Bearer ";

    private Key secretKeySpec;

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final MemberTokenRepository memberTokenRepository;
    private final MemberTokenQueryRepository memberTokenQueryRepository;

    @PostConstruct
    private void initKey(){
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        secretKeySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * AccessToken 생성
     * @param email 사용자 이메일
     * @return
     */
    public ResponseTokenDTO createAccessToken(String email){

        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(accessExpiration);
        Date expiryDateAsDate = java.sql.Timestamp.valueOf(expiryDate);

        String accessToken = JWT.create()
                .withSubject(ACCESS_TOKEN)
                .withExpiresAt(expiryDateAsDate)
                .withClaim(USERID_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKeySpec.getEncoded()));

        return new ResponseTokenDTO(accessToken, expiryDate);

    }

    /**
     * RefreshToken 생성
     */
    public ResponseTokenDTO createRefreshToken(){

        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(refreshExpiration);
        Date expiryDateAsDate = java.sql.Timestamp.valueOf(expiryDate);

        String refreshToken = JWT.create()
                .withSubject(REFRESH_TOKEN)
                .withExpiresAt(expiryDateAsDate)
                .sign(Algorithm.HMAC512(secretKeySpec.getEncoded()));

        return new ResponseTokenDTO(refreshToken, expiryDate);

    }

    /**
     * RefreshToken으로 Token update
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) throws IOException {
        MemberToken memberToken = memberTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String memberEmail = memberTokenQueryRepository.findEmailByRefreshToken(refreshToken);

        if (memberEmail == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        ResponseTokenDTO reIssueRefreshToken = createRefreshToken();
        ResponseTokenDTO reIssueAccessToken = createAccessToken(memberEmail);

        memberToken.updateTokens(reIssueAccessToken.getToken(), reIssueAccessToken.getExpiryDate(),
                reIssueRefreshToken.getToken(), reIssueRefreshToken.getExpiryDate());
        memberTokenRepository.saveAndFlush(memberToken);

        sendAccessAndRefreshToken(response, reIssueAccessToken.getToken(), reIssueRefreshToken.getToken());
    }

    /**
     * AccessToken , RefreshToken 보내기
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {

        response.setStatus(SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseMemberTokenDTO loginMemberDto = new ResponseMemberTokenDTO(accessToken,refreshToken);
        String res = objectMapper.writeValueAsString(loginMemberDto);
        response.getWriter().write(res);
    }

    /**
     * 사용자의 Authentication 객체를 반환
     */
    public Authentication getAuthentication(String accessToken) {

        DecodedJWT decodedJWT = parseClaims(accessToken);

        String email = decodedJWT.getClaim(USERID_CLAIM).asString();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        UserDetails principal = new MemberAdapter(member);

        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    /**
     * 액세스 토큰에서 Claims를 파싱
     * @param accessToken
     * @return Claims
     */
    private DecodedJWT parseClaims(String accessToken) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKeySpec.getEncoded())).build();
            return verifier.verify(accessToken);
        } catch (TokenExpiredException e) {
            log.error("Token expired: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.error("Error verifying token: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 헤더에서 AccessToken 추출
     */
    public String extractAccessToken(HttpServletRequest request) {

        String accessToken = request.getHeader(accessHeader);
        if (StringUtils.hasText(accessToken) && accessToken.startsWith(BEARER)) {
            return accessToken.replace(BEARER, "");
        }
        return null;
    }

    /**
     * 헤더에서 RefreshToken 추출
     */
    public String extractRefreshToken(HttpServletRequest request) {

        String refreshToken = request.getHeader(refreshHeader);
        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith(BEARER)) {
            return refreshToken.replace(BEARER, "");
        }
        return null;
    }

    /**
     * 토큰 유효성 검사
     */
    @SneakyThrows
    public TokenStatus isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKeySpec.getEncoded())).build().verify(token);
            return TokenStatus.VALID;
        } catch (TokenExpiredException e) {
            log.error("토큰이 만료되었습니다.", e.getMessage(), e);
            throw e;
        } catch (JWTDecodeException | IllegalArgumentException e) {
            log.error("JWT 토큰 디코딩 중 오류가 발생하였습니다.", e.getMessage(), e);
            throw e;
        } catch (NoSuchElementException e) {
            log.error("사용자를 찾을 수 없습니다.");
            throw e;
        } catch (Exception e) {
            log.error("예기치 않은 오류가 발생하였습니다. : {}", e.getMessage(), e);
            throw e;
        }
    }

}
