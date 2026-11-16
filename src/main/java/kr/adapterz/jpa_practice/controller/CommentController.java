package kr.adapterz.jpa_practice.controller;

import kr.adapterz.jpa_practice.dto.comment.CommentResponse;
import kr.adapterz.jpa_practice.dto.comment.CreateCommentRequest;
import kr.adapterz.jpa_practice.dto.comment.UpdateCommentRequest;
import kr.adapterz.jpa_practice.entity.Comment;
import kr.adapterz.jpa_practice.security.CustomUserDetails;
import kr.adapterz.jpa_practice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        request.setPostId(postId);
        Long writerId = userDetails.getUser().getId();
        Comment comment = commentService.create(request, writerId);

        URI location = URI.create("/api/v1/posts/" + postId + "/comments/" + comment.getId());

        return ResponseEntity.created(location).body(CommentResponse.of(comment));
    }

    //{postId} 댓글 목록 조회
    @GetMapping
    public List<CommentResponse> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.findAllByPostId(postId);
        List<CommentResponse> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(CommentResponse.of(comment));
        }
        return result;
    }

    //{commentId} 댓글 조회
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> get(@PathVariable Long postId, @PathVariable Long commentId) {
        try {
            Comment comment = commentService.findById(commentId);
            return ResponseEntity.ok(CommentResponse.of(comment)); //200+데이터
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    //댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long currentUserId = userDetails.getUser().getId();
            Comment comment = commentService.update(commentId, request, currentUserId);
            return ResponseEntity.ok(CommentResponse.of(comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();  // 404
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();  // 403 Forbidden
        }
    }

    //댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Long currentUserId = userDetails.getUser().getId();
            commentService.delete(commentId, currentUserId);
            return ResponseEntity.noContent().build();  // 204 No Content
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();  // 403 Forbidden
        }
    }
}
