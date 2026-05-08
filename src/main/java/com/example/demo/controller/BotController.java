package com.example.demo.controller;

import com.example.demo.entity.Bot;
import com.example.demo.repository.BotRepository;
import com.example.demo.dto.dtos.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
@RequiredArgsConstructor
public class BotController {

    private final BotRepository botRepository;

    // ================= CREATE BOT =================
    @PostMapping
    public ResponseEntity<ApiResponse> createBot(@RequestBody Bot bot) {

        Bot saved = botRepository.save(bot);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bot created successfully", saved));
    }

    // ================= GET ALL BOTS =================
    @GetMapping
    public ResponseEntity<ApiResponse> getAllBots() {

        List<Bot> bots = botRepository.findAll();

        return ResponseEntity.ok(
                ApiResponse.ok("All bots fetched", bots)
        );
    }

    // ================= GET BOT BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBotById(@PathVariable Long id) {

        Bot bot = botRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bot not found"));

        return ResponseEntity.ok(
                ApiResponse.ok("Bot fetched", bot)
        );
    }
}