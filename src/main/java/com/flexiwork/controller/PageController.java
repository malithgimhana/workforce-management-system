package com.flexiwork.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/about")
    public String about() { return "forward:/about.html"; }

    @GetMapping("/services")
    public String services() { return "forward:/services.html"; }

    @GetMapping("/contact")
    public String contact() { return "forward:/contact.html"; }

    @GetMapping("/login")
    public String login() { return "forward:/login.html"; }

    @GetMapping("/register")
    public String register() { return "forward:/register.html"; }

    @GetMapping("/worker/dashboard")
    public String workerDashboard() { return "forward:/worker-dashboard.html"; }

    @GetMapping("/employer/dashboard")
    public String employerDashboard() { return "forward:/employer-dashboard.html"; }

    @GetMapping("/admin")
    public String admin() { return "forward:/admin.html"; }

    @GetMapping("/qr-scanner")
    public String qrScanner() { return "forward:/qr-scanner.html"; }
}
