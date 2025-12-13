// DepartmentResponseDto.java
package com.pradeep.ems.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DepartmentResponseDto {

    private Long id;
    private String name;
    private String description;
    private String location;
    private BigDecimal budget;
    private Integer headCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    // Optional: Include employees when requested
    private List<EmployeeResponseDto> employees;
}