package com.pradeep.ems.scheduler;

import com.pradeep.ems.entity.Employee;
import com.pradeep.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler to check for employees added on a specific date
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeScheduler {

    private final EmployeeRepository employeeRepository;

    @Value("${app.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    /**
     * Scheduled task that runs daily at 9:00 AM
     * Checks for employees added on the previous day
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
                log.info("No employees found for date: {}.", targetDate);
                return;
            }

            log.info("Found {} employee(s) added on {}.", 
                employees.size(), targetDate);

            log.info("Daily employee report completed for date: {}", targetDate);

        } catch (Exception e) {
            log.error("Error processing daily employee report", e);
        }
    }

    /**
     * Check for employees added on a specific date
     * This method can be called manually or scheduled for a specific date
     * 
     * @param date The date to fetch employees for
     */
    public void sendEmployeeReportForDate(LocalDate date) {
        try {
            log.info("Fetching employees added on: {}", date);

            List<Employee> employees = employeeRepository.findByCreatedDate(date);

            if (employees.isEmpty()) {
                log.info("No employees found for date: {}.", date);
                return;
            }

            log.info("Found {} employee(s) added on {}.", 
                employees.size(), date);

            log.info("Employee report completed for date: {}", date);

        } catch (Exception e) {
            log.error("Error processing employee report for date: {}", date, e);
            throw new RuntimeException("Failed to process employee report: " + e.getMessage(), e);
        }
    }
}








