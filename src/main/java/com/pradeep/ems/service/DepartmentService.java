package com.pradeep.ems.service;

import com.pradeep.ems.dto.request.DepartmentRequestDto;
import com.pradeep.ems.dto.response.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {

    List<DepartmentResponseDto> getAllDepartments();

    DepartmentResponseDto getDepartmentById(Long id);

    DepartmentResponseDto createDepartment(DepartmentRequestDto departmentRequestDto);

    DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto departmentRequestDto);

    void deleteDepartment(Long id);

    //DepartmentResponseDto getDepartmentWithEmployees(Long id);
}
