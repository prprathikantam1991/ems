package com.pradeep.ems.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_employee_email", columnList = "email"),
    @Index(name = "idx_employee_id", columnList = "employee_id"),
    @Index(name = "idx_employee_department", columnList = "department_id"),
    @Index(name = "idx_employee_status", columnList = "status")
})
@NamedEntityGraph(
    name = "Employee.withDepartment",
    attributeNodes = @NamedAttributeNode("department")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "employee_id", unique = true, length = 20)
    private String employeeId; // e.g. EMP001

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, TERMINATED

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 200)
    private String address;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Positive(message = "Salary must be positive")
    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_employee_department"))
    private Department department;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version; // Optimistic locking
}
