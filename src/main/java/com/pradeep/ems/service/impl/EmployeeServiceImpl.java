package com.pradeep.ems.service.impl;

import com.pradeep.ems.dto.request.EmployeeRequestDto;
import com.pradeep.ems.dto.request.UpdateEmployeeRequestDto;
import com.pradeep.ems.dto.response.EmployeeResponseDto;
import com.pradeep.ems.entity.Department;
import com.pradeep.ems.entity.Employee;
import com.pradeep.ems.exception.ResourceNotFoundException;
import com.pradeep.ems.repository.DepartmentRepository;
import com.pradeep.ems.repository.EmployeeRepository;
import com.pradeep.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional(readOnly = true)  // Read-only operation - optimizes performance
    public Page<EmployeeResponseDto> getAllEmployees(Pageable pageable, String search, Long departmentId) {
        Specification<Employee> spec = Specification.where(null);
        
        // Demonstrate Specifications - dynamic query building
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            Specification<Employee> searchSpec = (root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern),
                    cb.like(cb.lower(root.get("employeeId")), searchPattern)
                );
            spec = spec.and(searchSpec);
        }
        
        if (departmentId != null) {
            Specification<Employee> deptSpec = (root, query, cb) -> 
                cb.equal(root.get("department").get("id"), departmentId);
            spec = spec.and(deptSpec);
        }
        
        Page<Employee> page = spec == null ? 
            employeeRepository.findAll(pageable) : 
            employeeRepository.findAll(spec, pageable);
        
        return page.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)  // Read-only operation - optimizes performance
    @Cacheable(value = "employees", key = "#id")  // Cache result by employee ID
    public EmployeeResponseDto getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return mapToDto(emp);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"employees", "departmentEmployees"}, allEntries = true)  // Clear caches
    public EmployeeResponseDto createEmployee(EmployeeRequestDto requestDto) {
        Department dept = departmentRepository.findById(requestDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));
        Employee employee = Employee.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .employeeId(requestDto.getEmployeeId())
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : "ACTIVE")
                .phoneNumber(requestDto.getPhoneNumber())
                .address(requestDto.getAddress())
                .hireDate(requestDto.getHireDate())
                .salary(requestDto.getSalary())
                .jobTitle(requestDto.getJobTitle())
                .department(dept)
                .build();
        Employee saved = employeeRepository.save(employee);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"employees", "departmentEmployees"}, key = "#id")  // Evict specific employee
    public EmployeeResponseDto updateEmployee(Long id, UpdateEmployeeRequestDto requestDto) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (requestDto.getName() != null) emp.setName(requestDto.getName());
        if (requestDto.getEmail() != null) emp.setEmail(requestDto.getEmail());
        if (requestDto.getEmployeeId() != null) emp.setEmployeeId(requestDto.getEmployeeId());
        if (requestDto.getStatus() != null) emp.setStatus(requestDto.getStatus());
        if (requestDto.getPhoneNumber() != null) emp.setPhoneNumber(requestDto.getPhoneNumber());
        if (requestDto.getAddress() != null) emp.setAddress(requestDto.getAddress());
        if (requestDto.getHireDate() != null) emp.setHireDate(requestDto.getHireDate());
        if (requestDto.getSalary() != null) emp.setSalary(requestDto.getSalary());
        if (requestDto.getJobTitle() != null) emp.setJobTitle(requestDto.getJobTitle());
        if (requestDto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(requestDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            emp.setDepartment(dept);
        }
        Employee updated = employeeRepository.save(emp);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"employees", "departmentEmployees"}, key = "#id")  // Evict deleted employee
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)  // Read-only operation - optimizes performance
    public List<EmployeeResponseDto> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)  // Read-only operation - optimizes performance
    public List<EmployeeResponseDto> searchEmployees(String query) {
        // Use Specifications for dynamic search - demonstrates JPA Specifications
        Specification<Employee> spec = (root, queryBuilder, cb) -> {
            String searchPattern = "%" + query.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), searchPattern),
                cb.like(cb.lower(root.get("email")), searchPattern),
                cb.like(cb.lower(root.get("employeeId")), searchPattern)
            );
        };
        return employeeRepository.findAll(spec)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional  // Transaction boundary at service layer (best practice)
    public EmployeeResponseDto updateEmployeeStatus(Long id, String status) {
        // Demonstrate modifying query - updates database directly without loading entity
        // Both repository calls are in the same transaction
        int updated = employeeRepository.updateEmployeeStatus(id, status);
        if (updated == 0) {
            throw new RuntimeException("Employee not found");
        }
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToDto(emp);
    }

    private EmployeeResponseDto mapToDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .employeeId(employee.getEmployeeId())
                .status(employee.getStatus())
                .phoneNumber(employee.getPhoneNumber())
                .address(employee.getAddress())
                .hireDate(employee.getHireDate())
                .salary(employee.getSalary())
                .jobTitle(employee.getJobTitle())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .version(employee.getVersion())
                .build();
    }
}
