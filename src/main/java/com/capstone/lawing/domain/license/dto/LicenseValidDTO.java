package com.capstone.lawing.domain.license.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

@Getter
@JsonIgnoreProperties
public class LicenseValidDTO {

    private String organization;
    private String birthDate;
    private String licenseNo01;
    private String licenseNo02;
    private String licenseNo03;
    private String licenseNo04;
    private String serialNo;
    private String userName;
    private String licenseNumber;

    public LicenseValidDTO(String organization, String birthDate, String licenseNo01, String licenseNo02, String licenseNo03, String licenseNo04, String serialNo, String userName, String licenseNumber){
        this.organization = organization;
        this.birthDate = birthDate;
        this.licenseNo01 = licenseNo01;
        this.licenseNo02 = licenseNo02;
        this.licenseNo03 = licenseNo03;
        this.licenseNo04 = licenseNo04;
        this.serialNo = serialNo;
        this.userName = userName;
        this.licenseNumber = licenseNumber;
    }

}
