package com.example.demo.controller;

import com.example.demo.dto.dtos.*;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.service.PostService;
import com.example.demo.service.RedisGuardrailService;
import com.example.demo.dto.dtos.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final RedisGuardrailService redisService;

    // ================= CREATE POST =================
    @PostMapping
    public ResponseEntity<ApiResponse> createPost(@RequestBody CreatePostRequest req) {

        Post post = postService.createPost(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Post created successfully", post));
    }

    // ================= ADD COMMENT =================
    @PostMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest req) {

        Comment comment = postService.addComment(postId, req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Comment added successfully", comment));
    }

    // ================= LIKE POST =================
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse> likePost(
            @PathVariable Long postId,
            @RequestBody LikeRequest req) {

        postService.likePost(postId, req);

        // ✅ FIXED METHOD NAME
        Long score = redisService.getVirality(postId);

        return ResponseEntity.ok(
                ApiResponse.ok("Post liked! Virality score: " + score, score)
        );
    }

    // ================= POST STATS =================
    @GetMapping("/{postId}/stats")
    public ResponseEntity<ApiResponse> getStats(@PathVariable Long postId) {

        // ✅ FIXED METHOD NAME
        Long virality = redisService.getVirality(postId);

        // optional fallback since no direct getBotCount exists
        Map<String, Object> stats = Map.of(
                "postId", postId,
                "viralityScore", virality
        );

        return ResponseEntity.ok(
                ApiResponse.ok("Post stats fetched", stats)
        );
    }
}