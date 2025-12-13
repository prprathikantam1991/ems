package com.pradeep.ems.controller;

import com.pradeep.ems.scheduler.EmployeeScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for manually triggering employee reports
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
public class EmployeeReportController {

    private final EmployeeScheduler employeeScheduler;

    @Value("${app.scheduler.email.recipient:admin@ems.com}")
    private String defaultRecipientEmail;

    /**
     * Manually trigger employee report for a specific date
     * 
     * @param date The date to fetch employees for (format: yyyy-MM-dd)
     * @param recipientEmail Optional recipient email (defaults to configured email)
     * @return Success message
     */
    @PostMapping("/employees/date/{date}")
    public ResponseEntity<String> generateReportForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String recipientEmail) {
        
        try {
            String email = recipientEmail != null ? recipientEmail : defaultRecipientEmail;
            log.info("Manual report generation requested for date: {}, recipient: {}", date, email);
            
            employeeScheduler.sendEmployeeReportForDate(date, email);
            
            return ResponseEntity.ok(
                String.format("Employee report for %s sent successfully to %s", 
                    date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy")), 
                    email)
            );
        } catch (Exception e) {
            log.error("Error generating report for date: {}", date, e);
            return ResponseEntity.internalServerError()
                .body("Failed to generate report: " + e.getMessage());
        }
    }

    /**
     * Get information about the scheduler configuration
     * 
     * @return Scheduler configuration details
     */
    @GetMapping("/scheduler/info")
    public ResponseEntity<SchedulerInfo> getSchedulerInfo() {
        SchedulerInfo info = new SchedulerInfo();
        info.setDefaultRecipient(defaultRecipientEmail);
        info.setMessage("Scheduler runs daily at 9:00 AM (configurable via app.scheduler.cron)");
        return ResponseEntity.ok(info);
    }

    // DTO for scheduler info
    public static class SchedulerInfo {
        private String defaultRecipient;
        private String message;

        public String getDefaultRecipient() {
            return defaultRecipient;
        }

        public void setDefaultRecipient(String defaultRecipient) {
            this.defaultRecipient = defaultRecipient;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}


