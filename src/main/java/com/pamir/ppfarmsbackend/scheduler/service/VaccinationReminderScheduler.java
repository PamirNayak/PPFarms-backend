package com.pamir.ppfarmsbackend.scheduler.service;

import com.pamir.ppfarmsbackend.health.entity.VaccinationRecord;
import com.pamir.ppfarmsbackend.health.repository.VaccinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class VaccinationReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(VaccinationReminderScheduler.class);

    private final VaccinationRepository vaccinationRepository;

    public VaccinationReminderScheduler(VaccinationRepository vaccinationRepository) {
        this.vaccinationRepository = vaccinationRepository;
    }

    /**
     * Daily Cron at 07:00 AM: Scans upcoming vaccination booster due dates within 7 days.
     */
    @Scheduled(cron = "0 0 7 * * ?")
    public void scanVaccinationDueReminders() {
        log.info("[CRON] Running Daily Vaccination Due Date Scan...");
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAhead = today.plusDays(7);

        List<VaccinationRecord> records = vaccinationRepository.findAll();
        int upcomingReminders = 0;

        for (VaccinationRecord vr : records) {
            if (vr.getNextDueDate() != null && !vr.getNextDueDate().isBefore(today) && !vr.getNextDueDate().isAfter(sevenDaysAhead)) {
                upcomingReminders++;
                log.info("VACCINE REMINDER: Vaccine '{}' for Animal [{}] is DUE on {}",
                        vr.getVaccineName(), vr.getAnimalId(), vr.getNextDueDate());
            }
        }

        log.info("[CRON] Vaccination Due Date Scan Completed. Total upcoming boosters within 7 days: {}", upcomingReminders);
    }
}
