// DepartmentRequestDto.java
package com.pradeep.ems.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Data
public class DepartmentRequestDto {

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Positive(message = "Budget must be positive")
    @Digits(integer = 15, fraction = 2, message = "Budget must have at most 15 integer digits and 2 decimal places")
    private BigDecimal budget;
}