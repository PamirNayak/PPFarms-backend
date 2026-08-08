package com.pamir.ppfarmsbackend.shared.multitenancy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class TenantAspect {

    @PersistenceContext
    private EntityManager entityManager;

    private static final UUID SYSTEM_ORG_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Before("execution(* com.pamir.ppfarmsbackend..repository..*(..))")
    public void enableTenantFilter() {
        UUID currentTenant = TenantContext.getCurrentTenant();
        if (currentTenant != null && !SYSTEM_ORG_ID.equals(currentTenant)) {
            Session session = entityManager.unwrap(Session.class);
            if (session != null && session.getEnabledFilter("tenantFilter") == null) {
                session.enableFilter("tenantFilter").setParameter("tenantId", currentTenant);
            }
        }
    }
}
