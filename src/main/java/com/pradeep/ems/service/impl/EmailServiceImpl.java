package com.pradeep.ems.service.impl;

import com.pradeep.ems.entity.Employee;
import com.pradeep.ems.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:no-reply@ems.com}")
    private String fromEmail;

    @Override
    public void sendEmployeeDetailsEmail(String to, String subject, List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            log.info("No employees to send email for. Skipping email to: {}", to);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(buildEmailBody(employees));

            mailSender.send(message);
            log.info("Email sent successfully to: {} with {} employee(s)", to, employees.size());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendEmployeeDetailsEmail(String to, List<Employee> employees) {
        LocalDate today = LocalDate.now();
        String subject = String.format("Daily Employee Report - %s", 
            today.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        sendEmployeeDetailsEmail(to, subject, employees);
    }

    private String buildEmailBody(List<Employee> employees) {
        StringBuilder body = new StringBuilder();
        body.append("Employee Details Report\n");
        body.append("=".repeat(50)).append("\n\n");
        body.append(String.format("Total Employees: %d\n\n", employees.size()));

        int index = 1;
        for (Employee employee : employees) {
            body.append(String.format("Employee #%d:\n", index++));
            body.append(String.format("  Name: %s\n", employee.getName() != null ? employee.getName() : "N/A"));
            body.append(String.format("  Email: %s\n", employee.getEmail() != null ? employee.getEmail() : "N/A"));
            body.append(String.format("  Phone Number: %s\n", 
                employee.getPhoneNumber() != null ? employee.getPhoneNumber() : "N/A"));
            body.append(String.format("  Employee ID: %s\n", 
                employee.getEmployeeId() != null ? employee.getEmployeeId() : "N/A"));
            body.append(String.format("  Department: %s\n", 
                employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A"));
            body.append(String.format("  Job Title: %s\n", 
                employee.getJobTitle() != null ? employee.getJobTitle() : "N/A"));
            body.append(String.format("  Status: %s\n", 
                employee.getStatus() != null ? employee.getStatus() : "N/A"));
            body.append("\n");
        }

        body.append("=".repeat(50)).append("\n");
        body.append("This is an automated email from Employee Management System.\n");

        return body.toString();
    }
}


