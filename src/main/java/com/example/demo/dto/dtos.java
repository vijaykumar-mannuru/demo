package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class dtos {
    // =========================
    // CREATE USER REQUEST
    // =========================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {

        private String username;

        private boolean premium;
    }

    // =========================
    // CREATE BOT REQUEST
    // =========================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBotRequest {

        private String name;

        private String personaDescription;
    }

    // =========================
    // CREATE POST REQUEST
    // =========================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostRequest {

        private Long authorId;

        // USER or BOT
        private String authorType;

        private String content;
    }

    // =========================
    // CREATE COMMENT REQUEST
    // =========================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCommentRequest {

        private Long authorId;

        // USER or BOT
        private String authorType;

        private String content;

        // 0 = top-level comment
        private int depthLevel;

        // Used for bot cooldown logic
        private Long targetUserId;
    }

    // =========================
    // LIKE REQUEST
    // =========================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeRequest {

        // Human user ID
        private Long userId;
    }

    // =========================
    // COMMON API RESPONSE
    // =========================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {

        private boolean success;

        private String message;

        private Object data;

        public static ApiResponse ok(String message, Object data) {
            return new ApiResponse(true, message, data);
        }

        public static ApiResponse error(String message) {
            return new ApiResponse(false, message, null);
        }
    }
}
