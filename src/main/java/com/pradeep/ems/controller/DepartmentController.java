package com.pradeep.ems.controller;

import com.pradeep.ems.dto.request.DepartmentRequestDto;
import com.pradeep.ems.dto.response.ApiResponseDto;
import com.pradeep.ems.dto.response.DepartmentResponseDto;
import com.pradeep.ems.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Department Management", description = "APIs for managing departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Get all departments", description = "Retrieve all departments")
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartments() {
        List<DepartmentResponseDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieve a specific department by its ID")
    public ResponseEntity<DepartmentResponseDto> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDto department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @PostMapping
    @Operation(summary = "Create new department", description = "Create a new department  ")
    public ResponseEntity<ApiResponseDto<DepartmentResponseDto>> createDepartment(
            @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {

        DepartmentResponseDto createdDepartment = departmentService.createDepartment(departmentRequestDto);

        ApiResponseDto<DepartmentResponseDto> response = ApiResponseDto.<DepartmentResponseDto>builder()
                .success(true)
                .message("Department created successfully")
                .data(createdDepartment)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update department", description = "Update an existing department")
//    public ResponseEntity<ApiResponseDto<DepartmentResponseDto>> updateDepartment(
//            @PathVariable Long id,
//            @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {
//
//        DepartmentResponseDto updatedDepartment = departmentService.updateDepartment(id, departmentRequestDto);
//
//        ApiResponseDto<DepartmentResponseDto> response = ApiResponseDto.<DepartmentResponseDto>builder()
//                .success(true)
//                .message("Department updated successfully")
//                .data(updatedDepartment)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete department", description = "Delete a department")
//    public ResponseEntity<ApiResponseDto<String>> deleteDepartment(@PathVariable Long id) {
//        departmentService.deleteDepartment(id);
//
//        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
//                .success(true)
//                .message("Department deleted successfully")
//                .data("Department with ID: " + id + " has been deleted")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }

//    @GetMapping("/{id}/employees")
//    @Operation(summary = "Get department employees", description = "Get all employees in a specific department")
//    public ResponseEntity<DepartmentResponseDto> getDepartmentWithEmployees(@PathVariable Long id) {
//        DepartmentResponseDto department = departmentService.getDepartmentWithEmployees(id);
//        return ResponseEntity.ok(department);
//    }
}
