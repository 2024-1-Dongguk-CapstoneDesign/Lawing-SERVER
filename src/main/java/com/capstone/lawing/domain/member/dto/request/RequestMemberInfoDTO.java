package com.capstone.lawing.domain.member.dto.request;

import com.capstone.lawing.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMemberInfoDTO {

    private String name;
    private String email;
    private String birth;
    private String gender;


    public Member toEntity(RequestMemberInfoDTO requestMemberInfoDTO){

        return Member.builder()
                .name(requestMemberInfoDTO.getName())
                .email(requestMemberInfoDTO.getEmail())
                .birth(requestMemberInfoDTO.getBirth())
                .gender(requestMemberInfoDTO.getGender())
                .build();
    }
}
