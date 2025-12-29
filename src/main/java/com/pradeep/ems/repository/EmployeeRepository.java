package com.pradeep.ems.repository;

import com.pradeep.ems.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * EmployeeRepository demonstrates:
 * 1. Derived Query Methods (findByDepartmentId)
 * 2. JPQL Query with Aggregation (countEmployeesByDepartment)
 * 3. JPQL Query with Fetch Join (findByIdWithDepartment - prevents N+1 problem)
 * 4. Entity Graph (declarative eager loading - findByIdWithDepartmentGraph)
 * 5. Native SQL Query (findTopEarnersNative)
 * 6. Modifying Query with @Modifying (updateEmployeeStatus)
 * 7. Pagination (findByDepartmentId with Pageable)
 * 8. Specifications (via JpaSpecificationExecutor - for dynamic queries)
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    // 1. DERIVED QUERY METHOD - Spring Data JPA automatically generates query from method name
    List<Employee> findByDepartmentId(Long departmentId);
    
    // 2. PAGINATION - Returns Page instead of List, supports pagination and sorting
    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    // 3. JPQL QUERY WITH AGGREGATION - Custom query using JPQL, demonstrates aggregation
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    Long countEmployeesByDepartment(@Param("departmentId") Long departmentId);
    
    // 4. JPQL QUERY WITH FETCH JOIN - Prevents N+1 problem by eagerly loading related entity
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
    java.util.Optional<Employee> findByIdWithDepartment(@Param("id") Long id);

    // 4b. ENTITY GRAPH (DECLARATIVE) - Alternative to fetch join, declaratively specifies eager loading
    // Uses @NamedEntityGraph defined on Employee entity - more declarative approach
    // Can override standard methods like findById() - no @Query needed!
    @EntityGraph(value = "Employee.withDepartment", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    java.util.Optional<Employee> findById(Long id);

    // 5. NATIVE SQL QUERY - Direct SQL query, useful for complex database-specific operations
    @Query(value = "SELECT * FROM employees WHERE salary > :minSalary ORDER BY salary DESC LIMIT :limit", 
           nativeQuery = true)
    List<Employee> findTopEarnersNative(@Param("minSalary") java.math.BigDecimal minSalary, 
                                        @Param("limit") int limit);

    // 6. MODIFYING QUERY - Updates database directly, requires @Modifying
    // Note: @Transactional should be at service layer (best practice), not here
    @Modifying
    @Query("UPDATE Employee e SET e.status = :status WHERE e.id = :id")
    int updateEmployeeStatus(@Param("id") Long id, @Param("status") String status);

    // 7. FIND BY CREATION DATE - Find employees created on a specific date
    @Query("SELECT e FROM Employee e WHERE e.createdAt >= :startDate AND e.createdAt < :endDate")
    List<Employee> findByCreatedDate(@Param("startDate") java.time.LocalDateTime startDate, 
                                      @Param("endDate") java.time.LocalDateTime endDate);
    
    // 7b. FIND BY CREATION DATE (LocalDate) - Convenience method
    default List<Employee> findByCreatedDate(LocalDate date) {
        java.time.LocalDateTime startDate = date.atStartOfDay();
        java.time.LocalDateTime endDate = date.plusDays(1).atStartOfDay();
        return findByCreatedDate(startDate, endDate);
    }
}
