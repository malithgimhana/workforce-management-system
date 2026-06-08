package com.flexiwork.service;

import com.flexiwork.model.Company;
import com.flexiwork.model.CompanyUser;
import com.flexiwork.model.SecurityGuard;
import com.flexiwork.model.User;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.CompanyRepository;
import com.flexiwork.repository.CompanyUserRepository;
import com.flexiwork.repository.SecurityGuardRepository;
import com.flexiwork.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final SecurityGuardRepository securityGuardRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.name}")
    private String adminName;

    private String encodedAdminPassword;

    public CustomUserDetailsService(UserRepository userRepository,
                                    CompanyRepository companyRepository,
                                    CompanyUserRepository companyUserRepository,
                                    SecurityGuardRepository securityGuardRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyUserRepository = companyUserRepository;
        this.securityGuardRepository = securityGuardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        this.encodedAdminPassword = passwordEncoder.encode(adminPassword);
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Admin
        if (adminEmail.equalsIgnoreCase(identifier)) {
            return new UserPrincipal(0L, null, adminEmail, "FlexiWork", "Admin", "ADMIN", encodedAdminPassword);
        }

        // Company user (HR_MANAGER, IT_ADMIN, etc.)
        Optional<CompanyUser> cuOpt = companyUserRepository.findByEmail(identifier);
        if (cuOpt.isPresent()) {
            CompanyUser cu = cuOpt.get();
            return new UserPrincipal(
                cu.getCompanyUserId(),
                cu.getCompany().getCompanyId(),
                cu.getEmail(),
                cu.getName(), "",
                "EMPLOYER",
                cu.getRole() != null ? cu.getRole().name() : "HR_MANAGER",
                cu.getPassword()
            );
        }

        // Company (employer main account)
        Optional<Company> compOpt = companyRepository.findByEmail(identifier);
        if (compOpt.isPresent()) {
            Company c = compOpt.get();
            return new UserPrincipal(
                c.getCompanyId(),
                c.getCompanyId(),
                c.getEmail(),
                c.getName(), "",
                "EMPLOYER",
                c.getPassword()
            );
        }

        // Security Guard
        Optional<SecurityGuard> guardOpt = securityGuardRepository.findByEmail(identifier);
        if (guardOpt.isPresent()) {
            SecurityGuard g = guardOpt.get();
            if (!Boolean.TRUE.equals(g.getIsActive())) {
                throw new UsernameNotFoundException("Security guard account is deactivated");
            }
            return new UserPrincipal(
                g.getGuardId(),
                g.getCompany().getCompanyId(),
                g.getEmail(),
                g.getName(), "",
                "SECURITY",
                g.getPassword()
            );
        }

        // Worker (by email or phone)
        Optional<User> userOpt = userRepository.findByEmailOrPhone(identifier, identifier);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (Boolean.TRUE.equals(u.getIsDeleted())) {
                throw new UsernameNotFoundException("Account deactivated");
            }
            return new UserPrincipal(
                u.getUserId(),
                null,
                u.getEmail() != null ? u.getEmail() : u.getPhone(),
                u.getFirstName(),
                u.getLastName(),
                "WORKER",
                u.getPassword()
            );
        }

        throw new UsernameNotFoundException("User not found: " + identifier);
    }
}
