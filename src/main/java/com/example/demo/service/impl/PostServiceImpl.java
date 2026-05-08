package com.example.demo.service.impl;

import com.example.demo.dto.dtos.*;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;
import com.example.demo.repository.BotRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final BotRepository botRepository;

    // =====================================================
    // CREATE POST
    // =====================================================
    @Override
    @Transactional
    public Post createPost(CreatePostRequest req) {

        validateAuthor(
                req.getAuthorId(),
                req.getAuthorType()
        );

        Post post = Post.builder()
                .authorId(req.getAuthorId())
                .authorType(req.getAuthorType().toUpperCase())
                .content(req.getContent())
                .build();

        return postRepository.save(post);
    }

    // =====================================================
    // ADD COMMENT
    // =====================================================
    @Override
    @Transactional
    public Comment addComment(Long postId,
                              CreateCommentRequest req) {

        // check post exists
        postRepository.findById(postId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Post not found"
                        ));

        validateAuthor(
                req.getAuthorId(),
                req.getAuthorType()
        );

        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(req.getAuthorId())
                .authorType(req.getAuthorType().toUpperCase())
                .content(req.getContent())
                .depthLevel(req.getDepthLevel())
                .build();

        return commentRepository.save(comment);
    }

    // =====================================================
    // LIKE POST
    // =====================================================
    @Override
    @Transactional
    public void likePost(Long postId,
                         LikeRequest req) {

        if (!postRepository.existsById(postId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Post not found"
            );
        }

        userRepository.findById(req.getUserId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        ));
    }

    // =====================================================
    // VALIDATE AUTHOR
    // =====================================================
    private void validateAuthor(Long authorId,
                                String authorType) {

        if ("USER".equalsIgnoreCase(authorType)) {

            userRepository.findById(authorId)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "User not found"
                            ));

        } else if ("BOT".equalsIgnoreCase(authorType)) {

            botRepository.findById(authorId)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Bot not found"
                            ));

        } else {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "authorType must be USER or BOT"
            );
        }
    }
}