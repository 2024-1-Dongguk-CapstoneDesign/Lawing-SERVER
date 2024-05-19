package com.capstone.lawing.domain.member.component;

import com.capstone.lawing.domain.member.Member;
import com.capstone.lawing.domain.member.Repository.MemberRepository;
import com.capstone.lawing.domain.member.dto.request.RequestMemberInfoDTO;
import com.capstone.lawing.domain.member.dto.request.RequestTokenInfoDTO;
import com.capstone.lawing.domain.memberToken.MemberToken;
import com.capstone.lawing.domain.memberToken.Repository.MemberTokenRepository;
import com.capstone.lawing.domain.memberToken.dto.response.ResponseMemberTokenDTO;
import com.capstone.lawing.domain.memberToken.dto.response.ResponseTokenDTO;
import com.capstone.lawing.global.auth.service.JwtService;
import com.capstone.lawing.global.enumType.ErrorCode;
import com.capstone.lawing.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoSocialLogin {


    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final MemberTokenRepository memberTokenRepository;
    public ResponseMemberTokenDTO doKakaoLogin(String kakaoAccessToken) {

        return getMemberInfoFromKakao(kakaoAccessToken);

    }

    /**
     * AccessToken으로 사용자 정보 얻기
     * @param kakaoAccessToken 카카오 액세스 토큰
     */
    private ResponseMemberTokenDTO getMemberInfoFromKakao(String kakaoAccessToken) {

        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl("https://kapi.kakao.com/v2/user/me")
                    .defaultHeader("Authorization","Bearer "+ kakaoAccessToken)
                    .defaultHeader("Content-type","application/x-www-form-urlencoded;charset=utf-8")
                    .build();

            String responseBody = webClient.post().retrieve().bodyToMono(String.class).block();

            JSONObject accountObj = new JSONObject(responseBody).getJSONObject("kakao_account");
            return divideNewOrExist(accountObj);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.KAKAO_RESPONSE_FAILED);
        }

    }

    /**
     * 신규회원, 기존회원 구분 후 로그인 처리
     * @param accountObj
     * @return
     */
    public ResponseMemberTokenDTO divideNewOrExist(JSONObject accountObj){

        try {

            String email = accountObj.getString("email")+"[KAKAO]";
            String name = accountObj.getString("name");
            String birth = accountObj.getString("birthyear").substring(2) + accountObj.getString("birthday");
            String gender = accountObj.getString("gender");

            RequestMemberInfoDTO requestMemberInfoDTO = new RequestMemberInfoDTO(name,email,birth,gender);

            System.out.println(requestMemberInfoDTO);

            return memberRepository.findByEmail(email)
                    .map(this::existMemberUpdate)
                    .orElseGet(() -> newMemberSave(requestMemberInfoDTO));

        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(ErrorCode.KAKAO_RESPONSE_FAILED);
        }

    }

    /**
     * 신규 회원 회원가입 처리
     * @param requestMemberInfoDTO
     */
    public ResponseMemberTokenDTO newMemberSave(RequestMemberInfoDTO requestMemberInfoDTO){

        log.info("=====================신규회원 처리=====================");
        try{

            //Member 테이블에 회원 정보 저장
            Member member = memberRepository.save(requestMemberInfoDTO.toEntity(requestMemberInfoDTO));

            //신규 토큰 발급 후 MemberToken 테이블에 토큰 정보 저장
            ResponseTokenDTO responseAccessTokenDTO = jwtService.createAccessToken(requestMemberInfoDTO.getEmail());
            ResponseTokenDTO responseRefreshTokenDTO = jwtService.createRefreshToken();

            RequestTokenInfoDTO requestTokenInfoDTO = new RequestTokenInfoDTO(responseAccessTokenDTO.getToken(),responseAccessTokenDTO.getExpiryDate(),responseRefreshTokenDTO.getToken(),responseRefreshTokenDTO.getExpiryDate());

            memberTokenRepository.save(requestTokenInfoDTO.tokenInfoDTO(member,requestTokenInfoDTO));

            return new ResponseMemberTokenDTO(requestTokenInfoDTO.getAccessToken(),requestTokenInfoDTO.getRefreshToken());

        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(ErrorCode.KAKAO_JOIN_FAILED);
        }


    }

    /**
     * 기존 회원 로그인 처리
     * @param member
     */
    public ResponseMemberTokenDTO existMemberUpdate(Member member){

        log.info("=====================기존회원 처리=====================");

        try {
            MemberToken existingMemberToken = memberTokenRepository.findByMemberId(member.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_TOKEN_NOT_FOUND));

            //신규 토큰 발급 후 MemberToken 테이블에 토큰 정보 저장
            ResponseTokenDTO responseAccessTokenDTO = jwtService.createAccessToken(member.getEmail());
            ResponseTokenDTO responseRefreshTokenDTO = jwtService.createRefreshToken();

            RequestTokenInfoDTO requestTokenInfoDTO = new RequestTokenInfoDTO(responseAccessTokenDTO.getToken(),responseAccessTokenDTO.getExpiryDate(),responseRefreshTokenDTO.getToken(),responseRefreshTokenDTO.getExpiryDate());

            existingMemberToken.updateTokens(requestTokenInfoDTO.getAccessToken(), requestTokenInfoDTO.getAccessTokenExpiry(), requestTokenInfoDTO.getRefreshToken(), requestTokenInfoDTO.getRefreshTokenExpiry());
            memberTokenRepository.save(existingMemberToken);

            return new ResponseMemberTokenDTO(requestTokenInfoDTO.getAccessToken(),requestTokenInfoDTO.getRefreshToken());

        }catch (Exception e){
            throw new CustomException(ErrorCode.KAKAO_LOGIN_FAILED);
        }


    }


}

