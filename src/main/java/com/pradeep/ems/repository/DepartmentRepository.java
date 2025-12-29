package com.pradeep.ems.repository;

import com.pradeep.ems.entity.Department;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * DepartmentRepository demonstrates:
 * 1. Derived Query Method (findByName)
 * 2. JPQL Query with Fetch Join (findByIdWithEmployees - prevents N+1 problem)
 * 2b. Entity Graph (declarative eager loading - findByIdWithEmployeesGraph)
 * 3. JPQL Query with Collection Size (findEmptyDepartments - uses SIZE() function)
 * 4. Native SQL Query (findDepartmentsWithEmployeeCountAbove)
 * 5. Modifying Query (updateDepartmentBudget)
 * 6. Specifications (via JpaSpecificationExecutor - for dynamic queries)
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    // 1. DERIVED QUERY METHOD - Simple find by field
    Optional<Department> findByName(String name);

    // 2. JPQL QUERY WITH FETCH JOIN - Eagerly loads employees to prevent N+1 queries
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Optional<Department> findByIdWithEmployees(@Param("id") Long id);
    
    // 2b. ENTITY GRAPH (DECLARATIVE) - Alternative to fetch join, declaratively specifies eager loading
    // Uses @NamedEntityGraph defined on Department entity - more declarative approach
    // Can override standard methods like findById() - no @Query needed!
    @EntityGraph(value = "Department.withEmployees", type = EntityGraph.EntityGraphType.FETCH)
    @Override
    Optional<Department> findById(Long id);
    
    // 3. JPQL QUERY WITH COLLECTION SIZE - Uses SIZE() function to query collection size
    @Query("SELECT d FROM Department d WHERE SIZE(d.employees) = 0")
    java.util.List<Department> findEmptyDepartments();

    // 4. NATIVE SQL QUERY - Complex query using native SQL with subquery
    @Query(value = "SELECT d.* FROM departments d " +
                   "WHERE (SELECT COUNT(*) FROM employees e WHERE e.department_id = d.id) > :minCount", 
           nativeQuery = true)
    java.util.List<Department> findDepartmentsWithEmployeeCountAbove(@Param("minCount") int minCount);

    // 5. MODIFYING QUERY - Updates database directly, requires @Modifying
    // Note: @Transactional should be at service layer (best practice), not here
    @Modifying
    @Query("UPDATE Department d SET d.budget = :budget WHERE d.id = :id")
    int updateDepartmentBudget(@Param("id") Long id, @Param("budget") java.math.BigDecimal budget);
}
