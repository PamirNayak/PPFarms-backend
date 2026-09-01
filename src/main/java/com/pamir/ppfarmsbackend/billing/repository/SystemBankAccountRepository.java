package com.pamir.ppfarmsbackend.billing.repository;

import com.pamir.ppfarmsbackend.billing.entity.SystemBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SystemBankAccountRepository extends JpaRepository<SystemBankAccount, UUID> {
    List<SystemBankAccount> findByIsActiveTrue();
}
