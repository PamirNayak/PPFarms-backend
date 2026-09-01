package com.pamir.ppfarmsbackend.shared.seed;

import com.pamir.ppfarmsbackend.billing.entity.Plan;
import com.pamir.ppfarmsbackend.billing.repository.PlanRepository;
import com.pamir.ppfarmsbackend.herd.entity.Species;
import com.pamir.ppfarmsbackend.herd.repository.SpeciesRepository;
import com.pamir.ppfarmsbackend.identity.entity.Organization;
import com.pamir.ppfarmsbackend.identity.entity.Role;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import com.pamir.ppfarmsbackend.identity.repository.RoleRepository;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Essential System Bootstrap Seeder.
 * Runs in ALL environments (Dev, Test, Prod) to ensure core structural prerequisites exist:
 * 1. System Tenant (00000000-0000-0000-0000-000000000000)
 * 2. System Security Roles (SUPER_ADMIN, ADMIN, MANAGER, VET, WORKER)
 * 3. Root Super Admin account (configurable via SUPER_ADMIN_EMAIL / SUPER_ADMIN_PASSWORD in .env)
 * 4. Default SaaS Pricing Tiers (Trial, Starter, Pro Growth, Enterprise)
 * 5. Base Species Catalog (Goat, Cattle, Sheep, Pig)
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class SystemBootstrapSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemBootstrapSeeder.class);

    private final JdbcTemplate jdbcTemplate;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SpeciesRepository speciesRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${superadmin.email:superadmin@ppfarms.com}")
    private String superAdminEmail;

    @Value("${superadmin.password:SuperAdmin123!}")
    private String superAdminPassword;

    public static final UUID SYSTEM_ORG_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    @Transactional
    public void run(String... args) {
        bootstrapSystemOrganization();
        bootstrapRoles();
        bootstrapSuperAdmin();
        bootstrapDefaultPlans();
        bootstrapSpecies();
    }

    private void bootstrapSystemOrganization() {
        log.info("[SYSTEM BOOTSTRAP] Ensuring System Platform Organization exists...");
        jdbcTemplate.update("""
            INSERT INTO organizations (id, name, email, phone, address, currency, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
            ON CONFLICT (id) DO NOTHING
        """, SYSTEM_ORG_ID, "System Platform Admin", superAdminEmail, null, null, "INR", "ACTIVE");
    }

    private void bootstrapRoles() {
        createRoleIfNotExists("SUPER_ADMIN", "Platform Super Administrator", SYSTEM_ORG_ID);
        createRoleIfNotExists("ADMIN", "Farm Owner / Tenant Administrator", null);
        createRoleIfNotExists("MANAGER", "Farm Operations Manager", null);
        createRoleIfNotExists("VET", "Veterinarian Staff", null);
        createRoleIfNotExists("WORKER", "Farm Worker / Technician", null);
    }

    private void createRoleIfNotExists(String name, String description, UUID orgId) {
        if (roleRepository.findByName(name).isEmpty()) {
            Role role = Role.builder()
                    .name(name)
                    .description(description)
                    .organizationId(orgId)
                    .build();
            roleRepository.saveAndFlush(role);
        }
    }

    private void bootstrapSuperAdmin() {
        if (userRepository.findByEmail(superAdminEmail).isEmpty()) {
            log.info("[SYSTEM BOOTSTRAP] Provisioning initial Super Admin account ({})", superAdminEmail);
            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElseThrow();
            
            User superAdmin = User.builder()
                    .organizationId(SYSTEM_ORG_ID)
                    .role(superAdminRole)
                    .name("Platform Super Admin")
                    .email(superAdminEmail)
                    .passwordHash(passwordEncoder.encode(superAdminPassword))
                    .status("ACTIVE")
                    .build();
            userRepository.saveAndFlush(superAdmin);
        }
    }

    private void bootstrapDefaultPlans() {
        if (planRepository.count() == 0) {
            log.info("[SYSTEM BOOTSTRAP] Creating default SaaS Subscription Plans...");
            Plan freeTrial = Plan.builder()
                    .name("14-Day Free Trial")
                    .planType("FREE")
                    .price(BigDecimal.ZERO)
                    .maxAnimals(50)
                    .maxUsers(2)
                    .features("{\"milkLogging\":true,\"vetModule\":true,\"pedigree\":true,\"accounting\":false,\"allowedSpecies\":[\"GOAT\"],\"multiSpecies\":false,\"maxSpeciesCount\":1}")
                    .isActive(true)
                    .build();

            Plan starter = Plan.builder()
                    .name("Commercial Starter")
                    .planType("MONTHLY")
                    .price(BigDecimal.valueOf(1499.00))
                    .maxAnimals(100)
                    .maxUsers(5)
                    .features("{\"milkLogging\":true,\"vetModule\":true,\"pedigree\":true,\"accounting\":true,\"reports\":true,\"allowedSpecies\":[\"GOAT\",\"SHEEP\"],\"multiSpecies\":true,\"maxSpeciesCount\":2}")
                    .isActive(true)
                    .build();

            Plan proGrowth = Plan.builder()
                    .name("Commercial Pro Growth")
                    .planType("MONTHLY")
                    .price(BigDecimal.valueOf(3499.00))
                    .maxAnimals(500)
                    .maxUsers(15)
                    .features("{\"milkLogging\":true,\"vetModule\":true,\"pedigree\":true,\"accounting\":true,\"reports\":true,\"bulkOperations\":true,\"apiAccess\":true,\"allowedSpecies\":[\"ALL\"],\"multiSpecies\":true,\"maxSpeciesCount\":99}")
                    .isActive(true)
                    .build();

            Plan enterprise = Plan.builder()
                    .name("Enterprise Annual Suite")
                    .planType("ANNUAL")
                    .price(BigDecimal.valueOf(29999.00))
                    .maxAnimals(999999)
                    .maxUsers(999999)
                    .features("{\"milkLogging\":true,\"vetModule\":true,\"pedigree\":true,\"accounting\":true,\"reports\":true,\"bulkOperations\":true,\"apiAccess\":true,\"dedicatedSupport\":true,\"allowedSpecies\":[\"ALL\"],\"multiSpecies\":true,\"maxSpeciesCount\":99}")
                    .isActive(true)
                    .build();

            planRepository.saveAll(List.of(freeTrial, starter, proGrowth, enterprise));
        }
    }

    private void bootstrapSpecies() {
        if (speciesRepository.count() == 0) {
            log.info("[SYSTEM BOOTSTRAP] Creating standard livestock Species...");
            Species goat = Species.builder().name("Goat").gestationDays(150).heatCycleDays(21).supportsMilking(true).supportsShearing(false).build();
            Species cattle = Species.builder().name("Cattle").gestationDays(283).heatCycleDays(21).supportsMilking(true).supportsShearing(false).build();
            Species sheep = Species.builder().name("Sheep").gestationDays(147).heatCycleDays(17).supportsMilking(true).supportsShearing(true).build();
            Species pig = Species.builder().name("Pig").gestationDays(114).heatCycleDays(21).supportsMilking(false).supportsShearing(false).build();
            speciesRepository.saveAll(List.of(goat, cattle, sheep, pig));
        }
    }
}
