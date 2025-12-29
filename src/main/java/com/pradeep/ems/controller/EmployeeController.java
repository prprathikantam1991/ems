package com.pradeep.ems.controller;

import com.pradeep.ems.dto.request.EmployeeRequestDto;
import com.pradeep.ems.dto.request.UpdateEmployeeRequestDto;
import com.pradeep.ems.dto.response.ApiResponseDto;
import com.pradeep.ems.dto.response.EmployeeResponseDto;
import com.pradeep.ems.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees", description = "Retrieve all employees with pagination and sorting")
    public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EmployeeResponseDto> employees = employeeService.getAllEmployees(pageable, search, departmentId);

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Retrieve a specific employee by their ID")
    public ResponseEntity<EmployeeResponseDto> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    @Operation(summary = "Create new employee", description = "Create a new employee record")
    public ResponseEntity<ApiResponseDto<EmployeeResponseDto>> createEmployee(@Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeResponseDto createdEmployee = employeeService.createEmployee(employeeRequestDto);

        ApiResponseDto<EmployeeResponseDto> response = ApiResponseDto.<EmployeeResponseDto>builder()
                .success(true)
                .message("Employee created successfully")
                .data(createdEmployee)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update employee", description = "Update an existing employee record")
//    public ResponseEntity<ApiResponseDto<EmployeeResponseDto>> updateEmployee(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdateEmployeeRequestDto updateEmployeeRequestDto) {
//
//        EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(id, updateEmployeeRequestDto);
//
//        ApiResponseDto<EmployeeResponseDto> response = ApiResponseDto.<EmployeeResponseDto>builder()
//                .success(true)
//                .message("Employee updated successfully")
//                .data(updatedEmployee)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete employee", description = "Delete an employee record")
//    public ResponseEntity<ApiResponseDto<String>> deleteEmployee(@PathVariable Long id) {
//        employeeService.deleteEmployee(id);
//
//        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
//                .success(true)
//                .message("Employee deleted successfully")
//                .data("Employee with ID: " + id + " has been deleted")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get employees by department", description = "Retrieve all employees in a specific department")
    public ResponseEntity<List<EmployeeResponseDto>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<EmployeeResponseDto> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }
//
//    @GetMapping("/search")
//    @Operation(summary = "Search employees", description = "Search employees by name, email, or employee ID")
//    public ResponseEntity<List<EmployeeResponseDto>> searchEmployees(@RequestParam String query) {
//        List<EmployeeResponseDto> employees = employeeService.searchEmployees(query);
//        return ResponseEntity.ok(employees);
//    }
//
//    @PatchMapping("/{id}/status")
//    @Operation(summary = "Update employee status", description = "Update the status of an employee (ACTIVE/INACTIVE)")
//    public ResponseEntity<ApiResponseDto<EmployeeResponseDto>> updateEmployeeStatus(
//            @PathVariable Long id,
//            @RequestParam String status) {
//
//        EmployeeResponseDto updatedEmployee = employeeService.updateEmployeeStatus(id, status);
//
//        ApiResponseDto<EmployeeResponseDto> response = ApiResponseDto.<EmployeeResponseDto>builder()
//                .success(true)
//                .message("Employee status updated successfully")
//                .data(updatedEmployee)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
}
