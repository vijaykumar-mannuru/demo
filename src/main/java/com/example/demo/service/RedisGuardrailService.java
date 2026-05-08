package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGuardrailService {

    private final RedisTemplate<String, String> redisTemplate;

    // ================= KEYS =================
    private static final String VIRALITY_KEY = "post:%d:virality";
    private static final String BOT_COUNT_KEY = "post:%d:bot_count";
    private static final String COOLDOWN_KEY  = "cooldown:%d:%d";
    private static final String NOTIF_KEY     = "user:%d:notifs";

    private static final int MAX_BOT_REPLIES = 100;

    // ================= VIRALITY SCORE =================
    public void increaseVirality(Long postId, String type) {

        String key = String.format(VIRALITY_KEY, postId);

        long score = switch (type) {
            case "BOT_REPLY" -> 1;
            case "LIKE" -> 20;
            case "COMMENT" -> 50;
            default -> 0;
        };

        if (score > 0) {
            redisTemplate.opsForValue().increment(key, score);
            log.info("Virality updated for post {} by {}", postId, score);
        }
    }

    public Long getVirality(Long postId) {
        String val = redisTemplate.opsForValue()
                .get(String.format(VIRALITY_KEY, postId));

        return val == null ? 0L : Long.parseLong(val);
    }
    public Set<String> getAllPendingNotifKeys() {
        return redisTemplate.keys("user:*:notifs");
    }

    // ================= BOT LIMIT =================
    public boolean canBotReply(Long postId) {

        String key = String.format(BOT_COUNT_KEY, postId);

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count > MAX_BOT_REPLIES) {
            log.warn("Bot limit reached for post {}", postId);
            return false;
        }

        return true;
    }

    // ================= COOLDOWN =================
    public boolean checkCooldown(Long botId, Long userId) {

        String key = String.format(COOLDOWN_KEY, botId, userId);

        Boolean set = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofMinutes(10));

        return Boolean.TRUE.equals(set);
    }
    public List<String> popAllPendingNotifs(String key) {

        Long size = redisTemplate.opsForList().size(key);

        if (size == null || size == 0) {
            return Collections.emptyList();
        }

        List<String> messages = redisTemplate.opsForList().range(key, 0, size - 1);

        redisTemplate.delete(key);

        return messages == null ? Collections.emptyList() : messages;
    }

    // ================= NOTIFICATIONS =================
    public void addNotification(Long userId, String message) {

        String key = String.format(NOTIF_KEY, userId);

        redisTemplate.opsForList().rightPush(key, message);

        log.info("Notification added for user {}: {}", userId, message);
    }

    public List<String> getNotifications(Long userId) {

        String key = String.format(NOTIF_KEY, userId);

        Long size = redisTemplate.opsForList().size(key);

        if (size == null || size == 0) {
            return Collections.emptyList();
        }

        return redisTemplate.opsForList().range(key, 0, size - 1);
    }

    public void clearNotifications(Long userId) {
        redisTemplate.delete(String.format(NOTIF_KEY, userId));
    }
}