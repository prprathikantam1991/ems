package com.pradeep.ems.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments", indexes = {
    @Index(name = "idx_department_name", columnList = "name")
})
@NamedEntityGraph(
    name = "Department.withEmployees",
    attributeNodes = @NamedAttributeNode("employees")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department name is required")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "budget", precision = 15, scale = 2)
    private java.math.BigDecimal budget;

    @Column(name = "head_count")
    private Integer headCount;

    @OneToMany(mappedBy = "department", 
               cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, 
               orphanRemoval = false, 
               fetch = FetchType.LAZY)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version; // Optimistic locking

    // Helper method to manage bidirectional relationship
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }
}
