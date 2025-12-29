package com.pradeep.ems.service;

import com.pradeep.ems.dto.request.EmployeeRequestDto;
import com.pradeep.ems.dto.request.UpdateEmployeeRequestDto;
import com.pradeep.ems.dto.response.EmployeeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    Page<EmployeeResponseDto> getAllEmployees(Pageable pageable, String search, Long departmentId);

    EmployeeResponseDto getEmployeeById(Long id);

    EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto);

    EmployeeResponseDto updateEmployee(Long id, UpdateEmployeeRequestDto requestDto);

    void deleteEmployee(Long id);

    List<EmployeeResponseDto> getEmployeesByDepartment(Long departmentId);

    List<EmployeeResponseDto> searchEmployees(String query);

    EmployeeResponseDto updateEmployeeStatus(Long id, String status);
}
