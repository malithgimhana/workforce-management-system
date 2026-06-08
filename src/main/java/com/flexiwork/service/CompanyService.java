package com.flexiwork.service;

import com.flexiwork.dto.CompanyRegisterRequest;
import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Company;
import com.flexiwork.repository.CompanyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final FileUploadService fileUploadService;

    public CompanyService(CompanyRepository companyRepository, PasswordEncoder passwordEncoder,
                          EmailService emailService, FileUploadService fileUploadService) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.fileUploadService = fileUploadService;
    }

    public Company registerCompany(CompanyRegisterRequest request) {
        return registerCompany(request, null);
    }

    public Company registerCompany(CompanyRegisterRequest request, MultipartFile brCert) {
        if (companyRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }
        if (companyRepository.existsByBrNumber(request.getBrNumber())) {
            throw new BusinessException("BR Number already registered");
        }

        Company company = Company.builder()
                .name(request.getName())
                .brNumber(request.getBrNumber())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .password(passwordEncoder.encode(request.getPassword()))
                .isDeleted(false)
                .build();

        Company saved = companyRepository.save(company);

        // Upload BR certificate if provided during registration
        if (brCert != null && !brCert.isEmpty()) {
            String certPath = fileUploadService.uploadFile(brCert, "docs/br");
            saved.setBrCertPath(certPath);
            saved.setDocStatus(DocumentStatus.PENDING);
            saved.setDocSubmittedAt(LocalDateTime.now());
            saved = companyRepository.save(saved);
        }

        emailService.sendCompanyWelcome(saved.getEmail(), saved.getName());
        emailService.sendAdminDocAlert(saved.getName(), "Company Registration");
        return saved;
    }

    public Company findById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
    }

    public Company findByEmail(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with email: " + email));
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public void softDelete(Long companyId) {
        Company company = findById(companyId);
        company.setIsDeleted(true);
        companyRepository.save(company);
    }
}
