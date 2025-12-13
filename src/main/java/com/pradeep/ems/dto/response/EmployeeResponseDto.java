package com.pradeep.ems.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponseDto {

    private Long id;
    private String name;
    private String email;
    private String employeeId;
    private String status;
    private String phoneNumber;
    private String address;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String jobTitle;
    private Long departmentId;
    private String departmentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
