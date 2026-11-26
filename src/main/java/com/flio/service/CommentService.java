package com.flio.service;

import com.flio.dto.comment.CreateCommentRequest;
import com.flio.dto.comment.UpdateCommentRequest;
import com.flio.entity.Comment;
import com.flio.entity.post.Post;
import com.flio.entity.user.User;
import com.flio.repository.CommentRepository;
import com.flio.repository.PostRepository;
import com.flio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Comment create(CreateCommentRequest request, Long writerId) {
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("post not found"));
        Comment comment = new Comment(
                request.getContent(),
                request.getIsAnonymous(),
                post,
                writer
        );
        return commentRepository.save(comment);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("comment not found"));
    }

    public List<Comment> findAllByPostId(Long postId) {
        postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("post not found"));
        return commentRepository.findAllByPostIdWithWriter(postId);
    }

    @Transactional
    public Comment update(Long id, UpdateCommentRequest request, Long currentUserId) {
        Comment comment = findById(id);

        if (!comment.getWriter().getId().equals(currentUserId)) {
            throw new SecurityException("본인의 댓글만 수정할 수 있습니다.");
        }

        if (request.getContent() != null) comment.setContent(request.getContent());
        if (request.getIsAnonymous() != null) comment.setIsAnonymous(request.getIsAnonymous());

        return comment;
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        Comment comment = findById(id);

        if (!comment.getWriter().getId().equals(currentUserId)) {
            throw new SecurityException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
