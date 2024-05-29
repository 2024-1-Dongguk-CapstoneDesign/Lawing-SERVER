package com.capstone.lawing.global.config;

import com.capstone.lawing.global.auth.filter.ExceptionHandlerFilter;
import com.capstone.lawing.global.auth.filter.JwtAuthenticationFilter;
import com.capstone.lawing.global.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().requestMatchers(
                "/v3/api-docs",
                "/lawing/wagger-ui/**"
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests((authorize) -> authorize
                    .requestMatchers("/member/social/login", "/lawing/swagger", "/license/token", "/memberToken/reissue").permitAll()
                    .anyRequest().authenticated())

            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // JWT 토큰 예외 처리
            .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class);

        return http.build();

    }


}
