package com.capstone.lawing.domain.license.dto.request;

import com.capstone.lawing.domain.license.License;
import com.capstone.lawing.domain.license.dto.LicenseValidDTO;
import com.capstone.lawing.domain.member.Member;
import lombok.Data;

@Data
public class RequestLicenseInfoDTO {

    private Member member;
    private String ownerName;
    private String licenseNumber;
    private String licenseBirth;
    private String serialNumber;

    public License toEntity(Member member, LicenseValidDTO licenseValidDTO){

        return License.builder()
                .member(member)
                .ownerName(licenseValidDTO.getUserName())
                .licenseNumber(licenseValidDTO.getLicenseNo01()+licenseValidDTO.getLicenseNo02()+licenseValidDTO.getLicenseNo03()+licenseValidDTO.getLicenseNo04())
                .licenseBirth(licenseValidDTO.getBirthDate())
                .serialNumber(licenseValidDTO.getSerialNo())
                .build();

    }

}
