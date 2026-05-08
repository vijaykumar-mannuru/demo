package com.example.demo.service;

import com.example.demo.dto.dtos;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Post;

public interface PostService {
    Post createPost(dtos.CreatePostRequest req);

    Comment addComment(Long postId, dtos.CreateCommentRequest req);

    void likePost(Long postId, dtos.LikeRequest req);
}
