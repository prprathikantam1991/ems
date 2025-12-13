package com.pradeep.ems.service;

import com.pradeep.ems.entity.Employee;

import java.util.List;

/**
 * Service interface for sending emails
 */
public interface EmailService {
    
    /**
     * Send email with employee details
     * @param to Recipient email address
     * @param subject Email subject
     * @param employees List of employees to include in email
     */
    void sendEmployeeDetailsEmail(String to, String subject, List<Employee> employees);
    
    /**
     * Send email with employee details (default subject)
     * @param to Recipient email address
     * @param employees List of employees to include in email
     */
    void sendEmployeeDetailsEmail(String to, List<Employee> employees);
}


