package com.flexiwork.dto;

import jakarta.validation.constraints.*;

public class CompanyRegisterRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "BR number is required")
    private String brNumber;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    public CompanyRegisterRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrNumber() { return brNumber; }
    public void setBrNumber(String brNumber) { this.brNumber = brNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
