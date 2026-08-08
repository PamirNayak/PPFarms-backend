package com.pamir.ppfarmsbackend.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;
    private UUID organizationId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; } public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public static UserResponseBuilder builder() { return new UserResponseBuilder(); }
    public static class UserResponseBuilder {
        private final UserResponse resp = new UserResponse();
        public UserResponseBuilder id(UUID id) { resp.setId(id); return this; }
        public UserResponseBuilder organizationId(UUID organizationId) { resp.setOrganizationId(organizationId); return this; }
        public UserResponseBuilder name(String name) { resp.setName(name); return this; }
        public UserResponseBuilder email(String email) { resp.setEmail(email); return this; }
        public UserResponseBuilder phone(String phone) { resp.setPhone(phone); return this; }
        public UserResponseBuilder role(String role) { resp.setRole(role); return this; }
        public UserResponseBuilder status(String status) { resp.setStatus(status); return this; }
        public UserResponseBuilder createdAt(OffsetDateTime createdAt) { resp.setCreatedAt(createdAt); return this; }
        public UserResponse build() { return resp; }
    }
}
