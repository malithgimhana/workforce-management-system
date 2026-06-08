package com.flexiwork.service;

import com.flexiwork.dto.RegisterRequest;
import com.flexiwork.dto.UpdateProfileRequest;
import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.User;
import com.flexiwork.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final FileUploadService fileUploadService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService, FileUploadService fileUploadService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.fileUploadService = fileUploadService;
    }

    public User registerWorker(RegisterRequest request) {
        return registerWorker(request, null, null, null);
    }

    public User registerWorker(RegisterRequest request, MultipartFile photo,
                                MultipartFile nicFront, MultipartFile nicBack) {
        if (userRepository.existsByNic(request.getNic())) {
            throw new BusinessException("NIC already registered");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Phone number already registered");
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .nic(request.getNic())
                .phone(request.getPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .address(request.getAddress())
                .district(request.getDistrict())
                .isDeleted(false)
                .build();

        User saved = userRepository.save(user);

        // Upload profile photo and NIC documents if provided at registration
        boolean hasFiles = false;
        if (photo != null && !photo.isEmpty()) {
            try { saved.setPhoto(fileUploadService.uploadFile(photo, "photos")); hasFiles = true; } catch (Exception ignored) {}
        }
        if (nicFront != null && !nicFront.isEmpty()) {
            saved.setNicFrontPath(fileUploadService.uploadFile(nicFront, "docs/nic"));
            hasFiles = true;
        }
        if (nicBack != null && !nicBack.isEmpty()) {
            saved.setNicBackPath(fileUploadService.uploadFile(nicBack, "docs/nic"));
            hasFiles = true;
        }
        if (hasFiles) {
            if (saved.getNicFrontPath() != null && saved.getNicBackPath() != null) {
                saved.setDocStatus(DocumentStatus.PENDING);
                saved.setDocSubmittedAt(LocalDateTime.now());
            }
            saved = userRepository.save(saved);
        }

        if (saved.getEmail() != null && !saved.getEmail().isBlank()) {
            emailService.sendWorkerWelcome(saved.getEmail(), saved.getFirstName());
        }
        return saved;
    }

    public User findByIdentifier(String identifier) {
        return userRepository.findByEmailOrPhone(identifier, identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + identifier));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findRecentUsers(Pageable pageable) {
        return userRepository.findRecentUsers(pageable);
    }

    public void softDelete(Long userId) {
        User user = findById(userId);
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    public User updateProfile(Long userId, UpdateProfileRequest req) {
        User user = findById(userId);

        // Phone uniqueness check (skip if unchanged)
        if (req.getPhone() != null && !req.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(req.getPhone())) {
                throw new BusinessException("Phone number already in use");
            }
            user.setPhone(req.getPhone());
        }

        // Email uniqueness check (skip if unchanged)
        if (req.getEmail() != null && !req.getEmail().isBlank() && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new BusinessException("Email already in use");
            }
            user.setEmail(req.getEmail());
        }

        if (req.getFirstName() != null)  user.setFirstName(req.getFirstName());
        if (req.getLastName()  != null)  user.setLastName(req.getLastName());
        if (req.getAddress()   != null)  user.setAddress(req.getAddress());
        if (req.getDistrict()  != null)  user.setDistrict(req.getDistrict());
        if (req.getGender()    != null)  user.setGender(req.getGender());

        // Password change
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (req.getCurrentPassword() == null || req.getCurrentPassword().isBlank()) {
                throw new BusinessException("Current password is required to set a new password");
            }
            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException("Current password is incorrect");
            }
            if (req.getNewPassword().length() < 8) {
                throw new BusinessException("New password must be at least 8 characters");
            }
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }

        return userRepository.save(user);
    }

    public User updatePhoto(Long userId, String photoFilename) {
        User user = findById(userId);
        user.setPhoto(photoFilename);
        return userRepository.save(user);
    }
}
