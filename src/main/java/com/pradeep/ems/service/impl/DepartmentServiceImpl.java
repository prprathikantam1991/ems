package com.pradeep.ems.service.impl;

import com.pradeep.ems.dto.request.DepartmentRequestDto;
import com.pradeep.ems.dto.response.DepartmentResponseDto;
import com.pradeep.ems.entity.Department;
import com.pradeep.ems.entity.Employee;
import com.pradeep.ems.repository.DepartmentRepository;
import com.pradeep.ems.repository.EmployeeRepository;
import com.pradeep.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)  // Read-only operation - optimizes performance
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)  // Read-only operation - optimizes performance
    @Cacheable(value = "departments", key = "#id")  // Cache result by department ID
    public DepartmentResponseDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return convertToDto(department);
    }

    @Override
    @Transactional
    @CacheEvict(value = "departments", allEntries = true)  // Clear cache when creating new department
    public DepartmentResponseDto createDepartment(DepartmentRequestDto dto) {
        Department department = Department.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .budget(dto.getBudget())
                .headCount(0)
                .build();

        Department saved = departmentRepository.save(department);
        return convertToDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "departments", key = "#id")  // Evict specific department from cache
    public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto dto) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        if (dto.getName() != null) department.setName(dto.getName());
        if (dto.getDescription() != null) department.setDescription(dto.getDescription());
        if (dto.getLocation() != null) department.setLocation(dto.getLocation());
        if (dto.getBudget() != null) department.setBudget(dto.getBudget());
        
        // Update head count based on current employees
        long employeeCount = employeeRepository.countEmployeesByDepartment(id);
        department.setHeadCount((int) employeeCount);

        Department updated = departmentRepository.save(department);
        return convertToDto(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "departments", key = "#id")  // Evict deleted department from cache
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        departmentRepository.delete(department);
    }

//    @Override
//    public DepartmentResponseDto getDepartmentWithEmployees(Long id) {
//        Department department = departmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
//
//        List<Employee> employees = employeeRepository.findByDepartmentId(id);
//
//        return DepartmentResponseDto.builder()
//                .id(department.getId())
//                .name(department.getName())
//                .description(department.getDescription())
//                .employees(
//                        employees.stream()
//                                .map(emp -> {
//                                    return DepartmentResponseDto.EmployeeDto.builder()
//                                            .id(emp.getId())
//                                            .firstName(emp.getFirstName())
//                                            .lastName(emp.getLastName())
//                                            .email(emp.getEmail())
//                                            .build();
//                                })
//                                .collect(Collectors.toList())
//                )
//                .build();
//    }

    private DepartmentResponseDto convertToDto(Department department) {
        // Calculate head count if not set
        int headCount = department.getHeadCount();
        if (headCount == 0 && department.getId() != null) {
            headCount = employeeRepository.countEmployeesByDepartment(department.getId()).intValue();
        }
        
        return DepartmentResponseDto.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .location(department.getLocation())
                .budget(department.getBudget())
                .headCount(headCount)
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .version(department.getVersion())
                .build();
    }
}
