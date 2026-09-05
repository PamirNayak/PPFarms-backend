package com.pamir.ppfarmsbackend.scheduler.service;

import com.pamir.ppfarmsbackend.health.entity.HealthRecord;
import com.pamir.ppfarmsbackend.health.repository.HealthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DrugWithdrawalAlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(DrugWithdrawalAlertScheduler.class);

    private final HealthRepository healthRepository;

    public DrugWithdrawalAlertScheduler(HealthRepository healthRepository) {
        this.healthRepository = healthRepository;
    }

    /**
     * Daily Cron at Midnight: Scans health records to log active/expiring drug withdrawal periods.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkDrugWithdrawalExpirations() {
        log.info("[CRON] Starting Daily Drug Withdrawal Safety Inspection...");
        LocalDate today = LocalDate.now();

        List<HealthRecord> allRecords = healthRepository.findAll();
        int activeMilkWithdrawals = 0;
        int activeSlaughterWithdrawals = 0;

        for (HealthRecord hr : allRecords) {
            if (hr.getMilkWithdrawalUntilDate() != null && !hr.getMilkWithdrawalUntilDate().isBefore(today)) {
                activeMilkWithdrawals++;
                log.info("HEALTH ALERT: Animal [{}] has ACTIVE MILK WITHDRAWAL until {}", hr.getAnimalId(), hr.getMilkWithdrawalUntilDate());
            }
            if (hr.getSlaughterWithdrawalUntilDate() != null && !hr.getSlaughterWithdrawalUntilDate().isBefore(today)) {
                activeSlaughterWithdrawals++;
                log.info("HEALTH ALERT: Animal [{}] has ACTIVE SLAUGHTER WITHDRAWAL until {}", hr.getAnimalId(), hr.getSlaughterWithdrawalUntilDate());
            }
        }

        log.info("[CRON] Drug Withdrawal Safety Scan Completed. Active Milk Blocks: {}, Active Slaughter Blocks: {}",
                activeMilkWithdrawals, activeSlaughterWithdrawals);
    }
}
