package com.capstone.lawing.domain.license.service;

import com.capstone.lawing.domain.license.License;
import com.capstone.lawing.domain.license.dto.LicenseValidDTO;
import com.capstone.lawing.domain.license.dto.request.RequestLicenseInfoDTO;
import com.capstone.lawing.domain.license.dto.response.CodefTokenResponseDto;
import com.capstone.lawing.domain.license.repository.LicenseRepository;
import com.capstone.lawing.domain.member.Member;
import com.capstone.lawing.domain.member.repository.MemberRepository;
import com.capstone.lawing.global.dto.Response.ResponseDTO;
import com.capstone.lawing.global.enumType.ErrorCode;
import com.capstone.lawing.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class LicenseService {

    @Value("${license.clientId}")
    String clientId;
    @Value("${license.clientSecret}")
    String clientSecret;
    @Value("${license.token}")
    String licenseToken;

    private final MemberRepository memberRepository;
    private final LicenseRepository licenseRepository;
    /**
     * 운전면허증 OCR
     * @param multipartFile 면허증 파일
     */
    public ResponseDTO getLicenseOCR(MultipartFile multipartFile , Member loginMember) throws Exception {

        log.info("=====================운전면허증 OCR=====================");

        // 이미지 데이터를 Base64 인코딩
        String base64Encoded = getBase64Image(multipartFile);

        HashMap<String, String> body = new HashMap<>();
        body.put("Type", "0");
        body.put("secret_mode", "0");
        body.put("IdCard_base64", base64Encoded);
        body.put("image_return", "0");

        WebClient webClient = WebClient.builder()
                .baseUrl("https://development.codef.io/v1/kr")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", licenseToken)
                .build();

        ResponseEntity<String> result = webClient.post().uri("/etc/a/ocr/drivers-license")
                .body(BodyInserters.fromValue(body)).retrieve().toEntity(String.class).block();

        JSONObject jsonObj = new JSONObject(URLDecoder.decode(result.getBody(), StandardCharsets.UTF_8));
        JSONObject resultObj = jsonObj.getJSONObject("result");

        log.info(jsonObj.toString());

        if (resultObj.get("code").equals("CF-00000") ) {

            LicenseValidDTO licenseValidDTO = getLicenseInfo(jsonObj , loginMember);

            return getLicenseValid(licenseValidDTO,loginMember);

        } else {
            throw new CustomException(ErrorCode.LICENSE_OCR_FAILED);
        }
    }

    /**
     * 이미지 파일 Base64 인코딩
     * @param multipartFile 운전 면허증 파일
     * @return 이미지 파일 Base64 인코딩 문자열
     */
    private String getBase64Image(MultipartFile multipartFile) throws IOException {

        log.info("====================이미지 파일 Base64 인코딩=====================");

        File license = File.createTempFile("License", null);

        try (FileOutputStream fos = new FileOutputStream(license)) {
            fos.write(multipartFile.getBytes());
        }

        FileInputStream imageInFile = new FileInputStream(license);
        byte[] LicenseImageData = new byte[(int) license.length()];
        imageInFile.read(LicenseImageData);

        // Base64 인코더 생성 및 패딩 제거 설정
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

        return encoder.encodeToString(LicenseImageData);
    }

    /**
     * OCR로 추출된 운전면허증 정보에서 필요한 정보 추출
     * JSONObject OCR API Response
     * @return LicenseValidDTO
     */
    private LicenseValidDTO getLicenseInfo(JSONObject jsonObj, Member loginMember) {

        log.info("====================OCR로 추출된 운전면허증 정보에서 필요한 정보 추출=====================");

        JSONObject dataObj = jsonObj.getJSONObject("data");

        String licenseNo = dataObj.get("resLicenseNo").toString();

        String birth = dataObj.getString("resUserIdentity").substring(0, 6);
        String name = dataObj.getString("resUserName");
        String gender = divideGender(dataObj.getString("resUserIdentity").substring(6,7));

        log.info(birth);
        log.info(name);
        log.info(gender);

        Member licenseMember = memberRepository.findByBirthAndNameAndGender(birth,name,gender).orElseThrow(() -> new CustomException(ErrorCode.LICENSE_MEMBER_INFO_NOT_CORRECT));

        log.info(loginMember.getId().toString());
        log.info(licenseMember.getId().toString());

        if(!loginMember.getId().equals(licenseMember.getId())){
            throw new CustomException(ErrorCode.LICENSE_MEMBER_INFO_NOT_CORRECT);
        }

        return new LicenseValidDTO(birth, licenseNo.substring(0, 2), licenseNo.substring(2, 4), licenseNo.substring(4, 10), licenseNo.substring(10), dataObj.get("resSerialNum").toString(), name);

    }


    /**
     * 성별 확정
     * @param genderChar
     * @return
     */
    public String divideGender(String genderChar) {

        log.info("====================성별 확정=====================");

        String gender = switch (genderChar) {
            case "1", "3" -> "male";
            case "2", "4" -> "female";
            default -> throw new CustomException(ErrorCode.WRONG_LICENSE_GENDER);
        };

        return gender;
    }

    /**
     * 운전면허증 유효성 검증 로직
     * @param licenseValidDTO 운전 면허증 정보
     * @return ResponseDTO
     */
    public ResponseDTO getLicenseValid(LicenseValidDTO licenseValidDTO , Member loginMember) {

        log.info("====================운전면허증 유효성 검증 로직=====================");

        licenseRepository.findBySerialNumber(licenseValidDTO.getSerialNo()).ifPresent(license -> {throw new CustomException(ErrorCode.DRIVERS_LICENSE_EXISTS);});

        HashMap<String, String> body = new HashMap<>();
        body.put("organization", "0001");
        body.put("birthDate", licenseValidDTO.getBirthDate());
        body.put("licenseNo01", licenseValidDTO.getLicenseNo01());
        body.put("licenseNo02", licenseValidDTO.getLicenseNo02());
        body.put("licenseNo03", licenseValidDTO.getLicenseNo03());
        body.put("licenseNo04", licenseValidDTO.getLicenseNo04());
        body.put("serialNo", licenseValidDTO.getSerialNo());
        body.put("userName", licenseValidDTO.getUserName());


        WebClient webClient = WebClient.builder()
                .baseUrl("https://development.codef.io/v1/kr")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", licenseToken)
                .build();

        ResponseEntity<String> result = webClient.post().uri("/public/ef/driver-license/KoRoad-status")
                .body(BodyInserters.fromValue(body)).retrieve().toEntity(String.class).block();

        JSONObject jsonObj = new JSONObject(URLDecoder.decode(result.getBody(), StandardCharsets.UTF_8));
        JSONObject data = jsonObj.getJSONObject("data");

        log.info(jsonObj.toString());

        if (data.get("resAuthenticity").equals("0") || data.get("resAuthenticity").equals("2")) {
            throw new CustomException(ErrorCode.LICENSE_VALIDATION_FAILED);
        }

        licenseRepository.save(new RequestLicenseInfoDTO().toEntity(loginMember,licenseValidDTO));

        return new ResponseDTO(200,HttpStatus.OK.name(),"SUCCESS","운전면허증 유효성 검증에 성공하였습니다.");
    }

    /**
     * codef Token 발급
     * @return CodefTokenResponseDto
     */
    public CodefTokenResponseDto getLicenseToken() {

        String auth = clientId + ":" + clientSecret;
        byte[] authEncBytes = Base64.getEncoder().encode(auth.getBytes());

        String authStringEnc = new String(authEncBytes);
        String authHeader = "Basic " + authStringEnc;

        WebClient webClient = WebClient.builder()
                .baseUrl("https://oauth.codef.io")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", authHeader)
                .build();

        return webClient.post()
                .uri("/oauth/token?grant_type=client_credentials&scope=read")
                .retrieve()
                .toEntity(CodefTokenResponseDto.class)
                .block()
                .getBody();

    }


}

