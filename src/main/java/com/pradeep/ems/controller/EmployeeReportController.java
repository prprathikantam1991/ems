package com.pradeep.ems.controller;

import com.pradeep.ems.scheduler.EmployeeScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Manually trigger employee report for a specific date
     * 
     * @param date The date to fetch employees for (format: yyyy-MM-dd)
     * @return Success message
     */
    @PostMapping("/employees/date/{date}")
    public ResponseEntity<String> generateReportForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            log.info("Manual report generation requested for date: {}", date);
            
            employeeScheduler.sendEmployeeReportForDate(date);
            
            return ResponseEntity.ok(
                String.format("Employee report for %s processed successfully", 
                    date.format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy")))
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
        info.setMessage("Scheduler runs daily at 9:00 AM (configurable via app.scheduler.cron)");
        return ResponseEntity.ok(info);
    }

    // DTO for scheduler info
    public static class SchedulerInfo {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}








