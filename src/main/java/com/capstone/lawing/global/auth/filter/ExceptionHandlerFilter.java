package com.capstone.lawing.global.auth.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.capstone.lawing.global.enumType.ErrorCode;
import com.capstone.lawing.global.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.NoSuchElementException;

import static com.capstone.lawing.global.auth.filter.JwtAuthenticationFilter.setErrorResponse;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    /**
     * 토큰 관련 에러 핸들링
     * JwtAuthenticationFilter 에서 발생하는 에러를 핸들링
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");

        try {
            filterChain.doFilter(request, response);

        } catch (TokenExpiredException e) {
            log.error("토큰이 만료되었습니다.");
            setErrorResponse(response, ErrorCode.TOKEN_EXPIRED);

        } catch (JwtException | IllegalArgumentException | JWTDecodeException | SignatureVerificationException e) {
            log.error("유효하지 않은 토큰입니다.");
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);

        } catch (NoSuchElementException e) {
            log.error("사용자를 찾을 수 없습니다.");
            setErrorResponse(response, ErrorCode.MEMBER_NOT_FOUND);

        } catch (ArrayIndexOutOfBoundsException e) {
            log.error("토큰을 추출할 수 없습니다.");
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);

        } catch (CustomException e) {
            log.error("사용자 정의 예외 발생: {}", e.getErrorCode());
            setErrorResponse(response, e.getErrorCode());

        } catch (Exception e) {
            log.error("예상치 못한 오류가 발생했습니다: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }



    }

}

