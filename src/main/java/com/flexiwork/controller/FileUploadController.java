package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.service.FileUploadService;
import com.flexiwork.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final UserService userService;

    public FileUploadController(FileUploadService fileUploadService, UserService userService) {
        this.fileUploadService = fileUploadService;
        this.userService = userService;
    }

    @PostMapping("/upload-photo")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        String filename = fileUploadService.uploadPhoto(file);
        userService.updatePhoto(principal.getId(), filename);
        Map<String, String> data = Map.of(
                "filename", filename,
                "url", "/uploads/" + filename
        );
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", data));
    }
}
