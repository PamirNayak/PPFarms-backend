package com.pamir.ppfarmsbackend.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Farm name is required")
    @Size(max = 150, message = "Farm name must be less than 150 characters")
    private String farmName;

    @NotBlank(message = "Owner name is required")
    @Size(max = 100, message = "Owner name must be less than 100 characters")
    private String ownerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    private String phone;
    private String address;
    private String currency = "INR";

    public String getFarmName() { return farmName; } public void setFarmName(String farmName) { this.farmName = farmName; }
    public String getOwnerName() { return ownerName; } public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; } public void setAddress(String address) { this.address = address; }
    public String getCurrency() { return currency; } public void setCurrency(String currency) { this.currency = currency; }
}
