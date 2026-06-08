package com.flexiwork.service;

import com.flexiwork.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");
    private static final Set<String> ALLOWED_DOC_TYPES   = Set.of("image/jpeg", "image/jpg", "image/png", "application/pdf");

    /** Upload a document (image or PDF) to a sub-directory. Returns relative path. */
    public String uploadFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) throw new BusinessException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE) throw new BusinessException("File size exceeds 10MB");
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_DOC_TYPES.contains(ct.toLowerCase()))
            throw new BusinessException("Only JPG, PNG, or PDF files are allowed");
        try {
            Path dir = Paths.get(uploadDir, subDir).toAbsolutePath();
            if (!Files.exists(dir)) Files.createDirectories(dir);
            String orig = file.getOriginalFilename();
            String ext = (orig != null && orig.contains(".")) ? orig.substring(orig.lastIndexOf(".")) : ".jpg";
            String filename = UUID.randomUUID() + ext;
            Path dest = dir.resolve(filename);
            Files.write(dest, file.getBytes());
            log.info("File uploaded: {}/{}", subDir, filename);
            return subDir + "/" + filename;
        } catch (IOException e) {
            throw new BusinessException("Failed to upload file: " + e.getMessage());
        }
    }

    public String uploadPhoto(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File size exceeds maximum allowed size of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("Only JPG and PNG files are allowed");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = UUID.randomUUID() + extension;

            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, file.getBytes());

            log.info("File uploaded: {}", filename);
            return filename;
        } catch (IOException e) {
            throw new BusinessException("Failed to upload file: " + e.getMessage());
        }
    }
}
