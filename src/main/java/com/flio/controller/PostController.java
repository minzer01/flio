package com.flio.controller;

import com.flio.dto.post.CreatePostRequest;
import com.flio.dto.post.PostResponse;
import com.flio.dto.post.UpdatePostRequest;
import com.flio.entity.post.Post;
import com.flio.security.CustomUserDetails;
import com.flio.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시글 목록 조회
    @GetMapping
    public List<PostResponse> getAll() {
        List<Post> posts = postService.findAll();
        List<PostResponse> result = new ArrayList<>();

        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            PostResponse dto = PostResponse.of(post);
            result.add(dto);
        }
        return result;
    }

    //게시글 작성
    @PostMapping
    public ResponseEntity<PostResponse> create(
            @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long writerId = userDetails.getUser().getId();
        Post post = postService.create(request, writerId);

        Long createdPostId = post.getId();
        URI location = URI.create("/api/v1/posts/" + createdPostId);
        return ResponseEntity.created(location).body(PostResponse.of(post));
    }

    //{postId} 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long postId) {
        try {
            Post post = postService.findById(postId);
            return ResponseEntity.ok(PostResponse.of(post)); //게시글 있으면 상태 코드 200 + 데이터 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); //게시글 없으면 상태 코드 404
        }
    }
    //게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> update(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long currentUserId = userDetails.getUser().getId();
            Post post = postService.update(postId, request, currentUserId);
            return ResponseEntity.ok(PostResponse.of(post));   // 200 OK
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();  // 404
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();  // 403 Forbidden
        }
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long currentUserId = userDetails.getUser().getId();
            postService.delete(postId, currentUserId);
            return ResponseEntity.noContent().build();  // 204 No Content
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();  // 403 Forbidden
        }
    }
}
