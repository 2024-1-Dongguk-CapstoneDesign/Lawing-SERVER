package com.capstone.lawing.global.auth.filter;

import com.capstone.lawing.domain.memberToken.TokenStatus;
import com.capstone.lawing.global.auth.service.JwtService;
import com.capstone.lawing.global.dto.Response.ResponseDTO;
import com.capstone.lawing.global.enumType.ErrorCode;
import com.capstone.lawing.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String LOGIN_URL = "/member/social/login";
    private static final String SWAGGER_UI_URL = "/lawing/swagger";

    private static final String CODEF_TOKEN_URL = "/license/token";
    private static final String TOKEN_REISSUE_URL = "/memberToken/reissue";

    private final JwtService jwtService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        String requestURI = request.getRequestURI();

        if(requestURI.contains(LOGIN_URL) || requestURI.contains(SWAGGER_UI_URL) || requestURI.contains(CODEF_TOKEN_URL)){
            filterChain.doFilter(request,response);
            return;
        }

        response.setCharacterEncoding("utf-8");

        String refreshToken = jwtService.extractRefreshToken(request);
        if (requestURI.contains(TOKEN_REISSUE_URL) && refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        if (refreshToken != null && jwtService.isTokenValid(refreshToken) == TokenStatus.VALID) {
            jwtService.checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return;
        }

        String accessToken = jwtService.extractAccessToken(request);
        if (accessToken == null) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
        }

        TokenStatus accessTokenStatus = jwtService.isTokenValid(accessToken);
        if (accessTokenStatus == TokenStatus.VALID) {
            saveAuthentication(accessToken);
            filterChain.doFilter(request, response);
        }

    }

    private void saveAuthentication(String accessToken) {
        Authentication authentication = jwtService.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Security Chain에서 발생하는 에러 응답 구성
     */
    public static void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ResponseDTO errorResponse = ResponseDTO.toResponseEntity(errorCode).getBody();

        if (errorResponse != null) {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(jsonResponse);
        }
    }

}
