package com.flexiwork.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final Long companyId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final String companyRole; // e.g. HR_MANAGER, IT_ADMIN, null for main company account
    private final String password;

    public UserPrincipal(Long id, Long companyId, String email,
                         String firstName, String lastName, String role, String password) {
        this(id, companyId, email, firstName, lastName, role, null, password);
    }

    public UserPrincipal(Long id, Long companyId, String email,
                         String firstName, String lastName, String role, String companyRole, String password) {
        this.id = id;
        this.companyId = companyId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.companyRole = companyRole;
        this.password = password;
    }

    public Long getId() { return id; }
    public Long getCompanyId() { return companyId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getRole() { return role; }
    public String getCompanyRole() { return companyRole; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
