package com.pradeep.ems.scheduler;

import com.pradeep.ems.entity.Employee;
import com.pradeep.ems.repository.EmployeeRepository;
import com.pradeep.ems.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Alternative scheduling strategies for Employee Scheduler
 * Demonstrates different scheduling approaches beyond cron
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeSchedulerAlternatives {

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final ThreadPoolTaskScheduler taskScheduler;

    @Value("${app.scheduler.email.recipient:admin@ems.com}")
    private String recipientEmail;

    @Value("${app.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    // ============================================
    // STRATEGY 1: FIXED RATE
    // ============================================
    // Runs at fixed intervals regardless of execution time
    // Use case: Regular monitoring/checking

    /**
     * Fixed Rate: Check for new employees every 5 minutes
     * Next execution starts 5 minutes after previous START, even if task is still running
     */
    @Scheduled(fixedRate = 300000, initialDelay = 60000)  // 5 minutes, wait 1 min after startup
    public void checkNewEmployeesFixedRate() {
        if (!schedulerEnabled) {
            return;
        }

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            List<Employee> employees = employeeRepository.findByCreatedDate(yesterday);

            if (!employees.isEmpty()) {
                log.info("Fixed Rate: Found {} new employees", employees.size());
                emailService.sendEmployeeDetailsEmail(recipientEmail, employees);
            }
        } catch (Exception e) {
            log.error("Fixed Rate: Error checking new employees", e);
        }
    }

    // ============================================
    // STRATEGY 2: FIXED DELAY
    // ============================================
    // Waits for previous execution to complete before starting next
    // Use case: Tasks that shouldn't overlap

    /**
     * Fixed Delay: Process employees every hour after previous completion
     * Ensures no overlapping executions
     */
    @Scheduled(fixedDelay = 3600000, initialDelay = 300000)  // 1 hour after completion, wait 5 min after startup
    public void processEmployeesFixedDelay() {
        if (!schedulerEnabled) {
            return;
        }

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            List<Employee> employees = employeeRepository.findByCreatedDate(yesterday);

            if (!employees.isEmpty()) {
                log.info("Fixed Delay: Processing {} employees", employees.size());
                // Simulate processing time
                Thread.sleep(5000);
                emailService.sendEmployeeDetailsEmail(recipientEmail, employees);
            }
        } catch (Exception e) {
            log.error("Fixed Delay: Error processing employees", e);
        }
    }

    // ============================================
    // STRATEGY 3: ASYNC SCHEDULING
    // ============================================
    // Execute scheduled tasks asynchronously
    // Use case: Long-running tasks

    /**
     * Async: Long-running task that doesn't block scheduler thread
     */
    @Scheduled(fixedRate = 600000)  // Every 10 minutes
    @Async
    public void asyncEmployeeReport() {
        if (!schedulerEnabled) {
            return;
        }

        log.info("Async: Starting employee report on thread: {}", Thread.currentThread().getName());

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            List<Employee> employees = employeeRepository.findByCreatedDate(yesterday);

            if (!employees.isEmpty()) {
                // Simulate long-running task
                Thread.sleep(30000);  // 30 seconds
                emailService.sendEmployeeDetailsEmail(recipientEmail, employees);
                log.info("Async: Report sent successfully");
            }
        } catch (Exception e) {
            log.error("Async: Error sending report", e);
        }
    }

    // ============================================
    // STRATEGY 4: DYNAMIC SCHEDULING
    // ============================================
    // Change schedule at runtime using TaskScheduler
    // Use case: Adaptive scheduling based on conditions

    private ScheduledFuture<?> dynamicTask;

    /**
     * Start dynamic scheduling with specified interval
     */
    public void startDynamicScheduling(long intervalMs) {
        stopDynamicScheduling();

        dynamicTask = taskScheduler.scheduleAtFixedRate(
            this::dynamicEmployeeCheck,
            Duration.ofMillis(intervalMs)
        );

        log.info("Dynamic scheduling started with interval: {}ms", intervalMs);
    }

    /**
     * Stop dynamic scheduling
     */
    public void stopDynamicScheduling() {
        if (dynamicTask != null) {
            dynamicTask.cancel(false);
            dynamicTask = null;
            log.info("Dynamic scheduling stopped");
        }
    }

    /**
     * Change interval at runtime
     */
    public void changeInterval(long newIntervalMs) {
        log.info("Changing interval to: {}ms", newIntervalMs);
        stopDynamicScheduling();
        startDynamicScheduling(newIntervalMs);
    }

    /**
     * Dynamic task that can be scheduled/unscheduled at runtime
     */
    private void dynamicEmployeeCheck() {
        if (!schedulerEnabled) {
            return;
        }

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            List<Employee> employees = employeeRepository.findByCreatedDate(yesterday);

            if (!employees.isEmpty()) {
                log.info("Dynamic: Found {} employees", employees.size());
                emailService.sendEmployeeDetailsEmail(recipientEmail, employees);
            }
        } catch (Exception e) {
            log.error("Dynamic: Error checking employees", e);
        }
    }

    // ============================================
    // STRATEGY 5: CONDITIONAL SCHEDULING
    // ============================================
    // Enable/disable based on conditions

    /**
     * Conditional: Only runs if scheduler is enabled
     * Can be enhanced with feature flags, environment checks, etc.
     */
    @Scheduled(fixedRate = 300000)
    public void conditionalEmployeeCheck() {
        // Additional conditions can be added here
        if (!schedulerEnabled) {
            log.debug("Conditional: Scheduler disabled, skipping");
            return;
        }

        // Check other conditions
        if (isBusinessHours()) {
            try {
                LocalDate yesterday = LocalDate.now().minusDays(1);
                List<Employee> employees = employeeRepository.findByCreatedDate(yesterday);

                if (!employees.isEmpty()) {
                    log.info("Conditional: Sending report during business hours");
                    emailService.sendEmployeeDetailsEmail(recipientEmail, employees);
                }
            } catch (Exception e) {
                log.error("Conditional: Error sending report", e);
            }
        } else {
            log.debug("Conditional: Outside business hours, skipping");
        }
    }

    private boolean isBusinessHours() {
        int hour = java.time.LocalTime.now().getHour();
        return hour >= 9 && hour < 17;  // 9 AM to 5 PM
    }

    // ============================================
    // STRATEGY 6: CONFIGURABLE RATE
    // ============================================
    // Rate from configuration properties

    /**
     * Configurable: Rate from properties with default fallback
     * Configure via: app.scheduler.rate=300000 (5 minutes)
     */
    @Scheduled(fixedRateString = "${app.scheduler.rate:300000}")
    public void configurableEmployeeCheck() {
        if (!schedulerEnabled) {
            return;
        }

        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            List<Employee> employees = employeeRepository.findByCreatedDate(yesterday);

            if (!employees.isEmpty()) {
                log.info("Configurable: Found {} employees", employees.size());
                emailService.sendEmployeeDetailsEmail(recipientEmail, employees);
            }
        } catch (Exception e) {
            log.error("Configurable: Error checking employees", e);
        }
    }

    // ============================================
    // STRATEGY 7: ONE-TIME SCHEDULED TASK
    // ============================================
    // Schedule a task to run once at a specific time

    /**
     * Schedule a one-time task to run at specific time
     */
    public void scheduleOneTimeReport(Instant executionTime, LocalDate targetDate) {
        taskScheduler.schedule(
            () -> {
                try {
                    List<Employee> employees = employeeRepository.findByCreatedDate(targetDate);
                    if (!employees.isEmpty()) {
                        emailService.sendEmployeeDetailsEmail(recipientEmail, employees);
                    }
                } catch (Exception e) {
                    log.error("One-time: Error sending report", e);
                }
            },
            executionTime
        );

        log.info("One-time report scheduled for: {}", executionTime);
    }
}


