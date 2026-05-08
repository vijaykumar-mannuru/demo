package com.example.demo.sheduler;

import com.example.demo.service.RedisGuardrailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class Notification {

    private final RedisGuardrailService redisService;

    @Scheduled(cron = "0 */5 * * * *")
    public void sweepPendingNotifications() {

        log.info("[SWEEPER] Starting...");

        Set<String> keys = redisService.getAllPendingNotifKeys();

        if (keys == null || keys.isEmpty()) {
            log.info("[SWEEPER] No notifications found.");
            return;
        }

        for (String key : keys) {

            String userId = key.split(":")[1];

            List<String> messages = redisService.popAllPendingNotifs(key);

            if (messages.isEmpty()) continue;

            log.info("[USER {}] {} notifications", userId, messages.size());

            for (String msg : messages) {
                log.info("[NOTIF] {}", msg);
            }
        }

        log.info("[SWEEPER] Completed for {} users", keys.size());
    }
}