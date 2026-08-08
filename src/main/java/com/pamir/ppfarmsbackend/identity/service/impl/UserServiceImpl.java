package com.pamir.ppfarmsbackend.identity.service.impl;

import com.pamir.ppfarmsbackend.identity.dto.NavigationMenuItemDto;
import com.pamir.ppfarmsbackend.identity.dto.UserRequest;
import com.pamir.ppfarmsbackend.identity.dto.UserResponse;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.RoleRepository;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.identity.service.UserService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyProfile(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NavigationMenuItemDto> getNavigationMenu(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var role = user.getRole().getName().toUpperCase();
        var menu = new ArrayList<NavigationMenuItemDto>();

        // Common for all authenticated users
        menu.add(new NavigationMenuItemDto("Dashboard", "/dashboard", "LayoutDashboard", "MAIN", 1));
        menu.add(new NavigationMenuItemDto("Livestock Herd", "/herd", "Box", "HERD", 2));

        var roleSpecificItems = switch (role) {
            case "SUPER_ADMIN" -> List.of(
                    new NavigationMenuItemDto("Housing Sheds", "/sheds", "Home", "HERD", 3),
                    new NavigationMenuItemDto("Milk Production", "/production", "Milk", "OPERATIONS", 4),
                    new NavigationMenuItemDto("Feed Inventory", "/feed", "Wheat", "OPERATIONS", 5),
                    new NavigationMenuItemDto("Health & Vet", "/health", "Stethoscope", "VET", 6),
                    new NavigationMenuItemDto("Reproduction", "/reproduction", "HeartHandshake", "VET", 7),
                    new NavigationMenuItemDto("Accounting Ledger", "/accounting", "Receipt", "FINANCE", 8),
                    new NavigationMenuItemDto("Sales & Orders", "/sales", "ShoppingBag", "FINANCE", 9),
                    new NavigationMenuItemDto("Executive Analytics", "/analytics", "BarChart3", "REPORTS", 10),
                    new NavigationMenuItemDto("Subscription Billing", "/billing", "CreditCard", "SYSTEM", 11),
                    new NavigationMenuItemDto("Super Admin Console", "/super-admin", "Crown", "SYSTEM", 12),
                    new NavigationMenuItemDto("Settings", "/settings", "Settings", "SYSTEM", 13)
            );
            case "ADMIN", "MANAGER" -> List.of(
                    new NavigationMenuItemDto("Housing Sheds", "/sheds", "Home", "HERD", 3),
                    new NavigationMenuItemDto("Milk Production", "/production", "Milk", "OPERATIONS", 4),
                    new NavigationMenuItemDto("Feed Inventory", "/feed", "Wheat", "OPERATIONS", 5),
                    new NavigationMenuItemDto("Health & Vet", "/health", "Stethoscope", "VET", 6),
                    new NavigationMenuItemDto("Reproduction", "/reproduction", "HeartHandshake", "VET", 7),
                    new NavigationMenuItemDto("Accounting Ledger", "/accounting", "Receipt", "FINANCE", 8),
                    new NavigationMenuItemDto("Sales & Orders", "/sales", "ShoppingBag", "FINANCE", 9),
                    new NavigationMenuItemDto("Executive Analytics", "/analytics", "BarChart3", "REPORTS", 10),
                    new NavigationMenuItemDto("Subscription Billing", "/billing", "CreditCard", "SYSTEM", 11),
                    new NavigationMenuItemDto("Settings", "/settings", "Settings", "SYSTEM", 12)
            );
            case "VET" -> List.of(
                    new NavigationMenuItemDto("Housing Sheds", "/sheds", "Home", "HERD", 3),
                    new NavigationMenuItemDto("Health & Vet", "/health", "Stethoscope", "VET", 4),
                    new NavigationMenuItemDto("Reproduction", "/reproduction", "HeartHandshake", "VET", 5),
                    new NavigationMenuItemDto("Executive Analytics", "/analytics", "BarChart3", "REPORTS", 6),
                    new NavigationMenuItemDto("Settings", "/settings", "Settings", "SYSTEM", 7)
            );
            default -> List.of( // WORKER
                    new NavigationMenuItemDto("Milk Production", "/production", "Milk", "OPERATIONS", 3),
                    new NavigationMenuItemDto("Feed Inventory", "/feed", "Wheat", "OPERATIONS", 4),
                    new NavigationMenuItemDto("Health & Vet", "/health", "Stethoscope", "VET", 5),
                    new NavigationMenuItemDto("Settings", "/settings", "Settings", "SYSTEM", 6)
            );
        };

        menu.addAll(roleSpecificItems);
        return menu;
    }

    @Override
    @Transactional
    public UserResponse updateMyProfile(UUID userId, String name, String phone) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (name != null) user.setName(name);
        if (phone != null) user.setPhone(phone);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse inviteStaff(UserRequest request, UUID tenantId) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        var role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Role " + request.getRole() + " not found"));

        var user = User.builder()
                .organizationId(tenantId)
                .role(role)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .build();

        return mapToResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getFarmStaff(UUID tenantId) {
        return userRepository.findByOrganizationIdAndDeletedAtIsNull(tenantId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse toggleUserStatus(UUID userId, UUID tenantId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Cannot modify user from another farm");
        }

        user.setStatus("ACTIVE".equalsIgnoreCase(user.getStatus()) ? "INACTIVE" : "ACTIVE");
        return mapToResponse(userRepository.save(user));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .organizationId(user.getOrganizationId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().getName())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}