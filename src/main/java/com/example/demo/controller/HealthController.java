package com.example.demo.controller;

import com.example.demo.dto.dtos.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    // ================= APP HEALTH CHECK =================
    @GetMapping
    public ResponseEntity<ApiResponse> health() {

        return ResponseEntity.ok(
                ApiResponse.ok("Application is running", "OK")
        );
    }

    // ================= SIMPLE SYSTEM INFO =================
    @GetMapping("/info")
    public ResponseEntity<ApiResponse> info() {

        Map<String, Object> data = Map.of(
                "app", "Demo Application",
                "status", "RUNNING",
                "version", "1.0"
        );

        return ResponseEntity.ok(
                ApiResponse.ok("System info fetched", data)
        );
    }
}