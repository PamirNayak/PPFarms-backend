package com.pamir.ppfarmsbackend.scheduler.service;

import com.pamir.ppfarmsbackend.feed.entity.FeedInventory;
import com.pamir.ppfarmsbackend.feed.repository.FeedInventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LowFeedStockAlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(LowFeedStockAlertScheduler.class);

    private final FeedInventoryRepository feedInventoryRepository;

    public LowFeedStockAlertScheduler(FeedInventoryRepository feedInventoryRepository) {
        this.feedInventoryRepository = feedInventoryRepository;
    }

    /**
     * Daily Cron at 06:00 AM: Scans feed inventory for low stock levels below minimum threshold.
     */
    @Scheduled(cron = "0 0 6 * * ?")
    public void scanLowFeedStockLevels() {
        log.info("[CRON] Running Morning Feed Inventory Reorder Check...");
        List<FeedInventory> allItems = feedInventoryRepository.findAll();

        int lowStockCount = 0;
        for (FeedInventory feed : allItems) {
            if (feed.getQuantityKg() != null && feed.getMinThresholdKg() != null &&
                    feed.getQuantityKg().compareTo(feed.getMinThresholdKg()) <= 0) {
                lowStockCount++;
                log.warn("FEED REORDER ALERT: Feed Item '{}' (Org: {}) current stock [{}] is AT OR BELOW threshold [{}]!",
                        feed.getFeedName(), feed.getOrganizationId(), feed.getQuantityKg(), feed.getMinThresholdKg());
            }
        }

        log.info("[CRON] Feed Inventory Reorder Check Finished. Items requiring reorder: {}", lowStockCount);
    }
}
