// JwtResponseDto.java
package com.pradeep.ems.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JwtResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private LocalDateTime expiresAt;
    private UserInfoDto user;

    @Data
    public static class UserInfoDto {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String role;
    }
}