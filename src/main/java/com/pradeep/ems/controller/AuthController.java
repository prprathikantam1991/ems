//// AuthController.java
//package com.pradeep.ems.controller;
//
//import com.pradeep.ems.dto.request.LoginRequestDto;
//import com.pradeep.ems.dto.request.RegisterRequestDto;
//import com.pradeep.ems.dto.response.ApiResponseDto;
//import com.pradeep.ems.dto.response.JwtResponseDto;
//import com.pradeep.ems.service.AuthService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
//@Tag(name = "Authentication", description = "APIs for authentication and authorization")
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/login")
//    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
//    public ResponseEntity<ApiResponseDto<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
//        JwtResponseDto jwtResponse = authService.login(loginRequestDto);
//
//        ApiResponseDto<JwtResponseDto> response = ApiResponseDto.<JwtResponseDto>builder()
//                .success(true)
//                .message("Login successful")
//                .data(jwtResponse)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/register")
//    @Operation(summary = "User registration", description = "Register a new user")
//    public ResponseEntity<ApiResponseDto<String>> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
//        authService.register(registerRequestDto);
//
//        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
//                .success(true)
//                .message("User registered successfully")
//                .data("Please contact administrator to activate your account")
//                .build();
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @PostMapping("/refresh")
//    @Operation(summary = "Refresh token", description = "Refresh JWT token")
//    public ResponseEntity<ApiResponseDto<JwtResponseDto>> refreshToken(@RequestParam String refreshToken) {
//        JwtResponseDto jwtResponse = authService.refreshToken(refreshToken);
//
//        ApiResponseDto<JwtResponseDto> response = ApiResponseDto.<JwtResponseDto>builder()
//                .success(true)
//                .message("Token refreshed successfully")
//                .data(jwtResponse)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/logout")
//    @Operation(summary = "User logout", description = "Logout user and invalidate token")
//    public ResponseEntity<ApiResponseDto<String>> logout(@RequestHeader("Authorization") String token) {
//        authService.logout(token);
//
//        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
//                .success(true)
//                .message("Logged out successfully")
//                .data("Token has been invalidated")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//}