package com.flexiwork.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null) {
            int status = Integer.parseInt(statusCode.toString());
            if (status == HttpStatus.NOT_FOUND.value()) return "forward:/404.html";
            if (status == HttpStatus.FORBIDDEN.value()) return "forward:/403.html";
            if (status == HttpStatus.UNAUTHORIZED.value()) return "forward:/401.html";
        }
        return "forward:/404.html";
    }
}
