// LoginRequestDto.java
package com.pradeep.ems.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}