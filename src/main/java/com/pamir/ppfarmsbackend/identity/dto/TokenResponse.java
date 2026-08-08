package com.pamir.ppfarmsbackend.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private UserSummary user;

    public String getAccessToken() { return accessToken; } public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; } public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getTokenType() { return tokenType; } public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public UserSummary getUser() { return user; } public void setUser(UserSummary user) { this.user = user; }

    public static TokenResponseBuilder builder() { return new TokenResponseBuilder(); }
    public static class TokenResponseBuilder {
        private final TokenResponse res = new TokenResponse();
        public TokenResponseBuilder accessToken(String accessToken) { res.setAccessToken(accessToken); return this; }
        public TokenResponseBuilder refreshToken(String refreshToken) { res.setRefreshToken(refreshToken); return this; }
        public TokenResponseBuilder tokenType(String tokenType) { res.setTokenType(tokenType); return this; }
        public TokenResponseBuilder user(UserSummary user) { res.setUser(user); return this; }
        public TokenResponse build() { return res; }
    }

    public static class UserSummary {
        private UUID id;
        private String name;
        private String email;
        private String role;
        private UUID organizationId;
        private String organizationName;

        public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
        public String getName() { return name; } public void setName(String name) { this.name = name; }
        public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; } public void setRole(String role) { this.role = role; }
        public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
        public String getOrganizationName() { return organizationName; } public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

        public static UserSummaryBuilder builder() { return new UserSummaryBuilder(); }
        public static class UserSummaryBuilder {
            private final UserSummary summary = new UserSummary();
            public UserSummaryBuilder id(UUID id) { summary.setId(id); return this; }
            public UserSummaryBuilder name(String name) { summary.setName(name); return this; }
            public UserSummaryBuilder email(String email) { summary.setEmail(email); return this; }
            public UserSummaryBuilder role(String role) { summary.setRole(role); return this; }
            public UserSummaryBuilder organizationId(UUID organizationId) { summary.setOrganizationId(organizationId); return this; }
            public UserSummaryBuilder organizationName(String organizationName) { summary.setOrganizationName(organizationName); return this; }
            public UserSummary build() { return summary; }
        }
    }
}
