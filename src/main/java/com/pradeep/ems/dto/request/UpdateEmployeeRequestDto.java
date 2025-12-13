// UpdateEmployeeRequestDto.java
package com.pradeep.ems.dto.request;

//import com.pradeep.ems.validation.ValidEmail;
//import com.pradeep.ems.validation.ValidPhone;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateEmployeeRequestDto {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20, message = "Employee ID must not exceed 20 characters")
    private String employeeId;

    @Pattern(regexp = "ACTIVE|INACTIVE|TERMINATED", message = "Status must be ACTIVE, INACTIVE, or TERMINATED")
    private String status;

    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    private LocalDate hireDate;

    @Positive(message = "Salary must be positive")
    @Digits(integer = 10, fraction = 2, message = "Salary must have at most 10 integer digits and 2 decimal places")
    private BigDecimal salary;

    @Size(max = 100, message = "Job title must not exceed 100 characters")
    private String jobTitle;

    private Long departmentId;
}