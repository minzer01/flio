package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.comment.CreateCommentRequest;
import kr.adapterz.jpa_practice.dto.comment.UpdateCommentRequest;
import kr.adapterz.jpa_practice.entity.Comment;
import kr.adapterz.jpa_practice.entity.post.Post;
import kr.adapterz.jpa_practice.entity.user.User;
import kr.adapterz.jpa_practice.repository.CommentRepository;
import kr.adapterz.jpa_practice.repository.PostRepository;
import kr.adapterz.jpa_practice.repository.UserRepository;
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
    public Comment create (CreateCommentRequest request) {
        User writer = userRepository.findById(request.getWriterId()).orElseThrow(() ->new IllegalArgumentException("user not found"));
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() ->new IllegalArgumentException("post not found"));
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
        return commentRepository.findAllByPostIdOrderByIdAsc(postId);
    }

    @Transactional
    public Comment update(Long id, UpdateCommentRequest request) {
        Comment comment = findById(id);

        if (request.getContent() != null) comment.setContent(request.getContent());
        if (request.getIsAnonymous() != null) comment.setIsAnonymous(request.getIsAnonymous());

        return comment;
    }

    @Transactional
    public void delete(Long id) {
        commentRepository.delete(findById(id));
    }
}
