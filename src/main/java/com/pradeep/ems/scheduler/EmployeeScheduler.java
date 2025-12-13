package com.pradeep.ems.scheduler;

import com.pradeep.ems.entity.Employee;
import com.pradeep.ems.repository.EmployeeRepository;
import com.pradeep.ems.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler to send daily email reports of employees added on a specific date
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeScheduler {

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @Value("${app.scheduler.email.recipient:admin@ems.com}")
    private String recipientEmail;

    @Value("${app.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    /**
     * Scheduled task that runs daily at 9:00 AM
     * Sends email with employees added on the previous day
     * 
     * Cron expression: second, minute, hour, day, month, weekday
     * "0 0 9 * * ?" = Every day at 9:00 AM
     */
    @Scheduled(cron = "${app.scheduler.cron:0 0 9 * * ?}")
    public void sendDailyEmployeeReport() {
        if (!schedulerEnabled) {
            log.debug("Scheduler is disabled. Skipping daily employee report.");
            return;
        }

        try {
            // Get employees added yesterday
            LocalDate targetDate = LocalDate.now().minusDays(1);
            log.info("Fetching employees added on: {}", targetDate);

            List<Employee> employees = employeeRepository.findByCreatedDate(targetDate);

            if (employees.isEmpty()) {
                log.info("No employees found for date: {}. Skipping email.", targetDate);
                return;
            }

            log.info("Found {} employee(s) added on {}. Sending email to: {}", 
                employees.size(), targetDate, recipientEmail);

            emailService.sendEmployeeDetailsEmail(recipientEmail, employees);

            log.info("Daily employee report sent successfully for date: {}", targetDate);

        } catch (Exception e) {
            log.error("Error sending daily employee report", e);
        }
    }

    /**
     * Scheduled task that runs daily at 9:00 AM for a specific date
     * This method can be called manually or scheduled for a specific date
     * 
     * @param date The date to fetch employees for
     * @param recipientEmail The email address to send the report to
     */
    public void sendEmployeeReportForDate(LocalDate date, String recipientEmail) {
        try {
            log.info("Fetching employees added on: {}", date);

            List<Employee> employees = employeeRepository.findByCreatedDate(date);

            if (employees.isEmpty()) {
                log.info("No employees found for date: {}. Skipping email.", date);
                return;
            }

            log.info("Found {} employee(s) added on {}. Sending email to: {}", 
                employees.size(), date, recipientEmail);

            String subject = String.format("Employee Report for %s", 
                date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            
            emailService.sendEmployeeDetailsEmail(recipientEmail, subject, employees);

            log.info("Employee report sent successfully for date: {}", date);

        } catch (Exception e) {
            log.error("Error sending employee report for date: {}", date, e);
            throw new RuntimeException("Failed to send employee report: " + e.getMessage(), e);
        }
    }
}


