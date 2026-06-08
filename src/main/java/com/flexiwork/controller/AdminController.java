package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.model.CommissionPayment;
import com.flexiwork.model.Company;
import com.flexiwork.model.Job;
import com.flexiwork.model.User;
import com.flexiwork.repository.ApplicationRepository;
import com.flexiwork.repository.CommissionPaymentRepository;
import com.flexiwork.service.CompanyService;
import com.flexiwork.service.JobService;
import com.flexiwork.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final ApplicationRepository applicationRepository;
    private final CommissionPaymentRepository commissionPaymentRepository;

    public AdminController(UserService userService, CompanyService companyService,
                           JobService jobService, ApplicationRepository applicationRepository,
                           CommissionPaymentRepository commissionPaymentRepository) {
        this.userService = userService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.applicationRepository = applicationRepository;
        this.commissionPaymentRepository = commissionPaymentRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Company>>> getAllCompanies() {
        List<Company> companies = companyService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Companies retrieved", companies));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("User deleted"));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable Long id) {
        companyService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.<Void>success("Company deleted"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        long totalWorkers      = userService.findAll().size();
        long totalCompanies    = companyService.findAll().size();
        long totalJobs         = jobService.findAll().size();
        long totalApplications = applicationRepository.count();
        long totalPayments     = commissionPaymentRepository.count();

        // Real earnings: sum of commissionAmount since start of current month
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        BigDecimal earningsThisMonth = commissionPaymentRepository.sumCommissionSince(startOfMonth);

        // Recent activity: last 5 commission payments + last 5 user registrations
        List<CommissionPayment> recentPayments = commissionPaymentRepository.findRecentPayments(PageRequest.of(0, 5));
        List<User> recentUsers = userService.findRecentUsers(PageRequest.of(0, 5));

        List<Map<String, Object>> activity = new ArrayList<>();
        for (CommissionPayment p : recentPayments) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", "payment");
            item.put("icon", "💰");
            item.put("text", "Commission LKR " + p.getCommissionAmount().toPlainString()
                    + " from " + (p.getCompany() != null ? p.getCompany().getName() : "company")
                    + " — " + (p.getJob() != null ? p.getJob().getTitle() : ""));
            item.put("time", p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            activity.add(item);
        }
        for (User u : recentUsers) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("type", "registration");
            item.put("icon", "👤");
            item.put("text", "Worker " + u.getFullName() + " registered");
            item.put("time", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
            activity.add(item);
        }
        // Sort all activity by time descending
        activity.sort((a, b) -> String.valueOf(b.get("time")).compareTo(String.valueOf(a.get("time"))));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalWorkers", totalWorkers);
        stats.put("totalCompanies", totalCompanies);
        stats.put("totalJobs", totalJobs);
        stats.put("totalApplications", totalApplications);
        stats.put("totalPayments", totalPayments);
        stats.put("earningsThisMonth", earningsThisMonth);
        stats.put("recentActivity", activity);

        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", stats));
    }
}
