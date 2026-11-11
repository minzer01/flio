package kr.adapterz.jpa_practice.service;

import kr.adapterz.jpa_practice.dto.post.CreatePostRequest;
import kr.adapterz.jpa_practice.dto.post.UpdatePostRequest;
import kr.adapterz.jpa_practice.entity.post.Post;
import kr.adapterz.jpa_practice.entity.user.User;
import kr.adapterz.jpa_practice.repository.PostRepository;
import kr.adapterz.jpa_practice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Post create (CreatePostRequest request) {
        User writer = userRepository.findById(request.getWriterId()).orElseThrow(() -> new IllegalArgumentException("user not found"));
        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                request.getCountry(),
                request.getThemes(),
                request.getMood(),
                request.getIsAnonymous(),
                writer
        );

        post.setImageUrl(request.getImageUrl());

        return postRepository.save(post);
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("post not found"));
    }

    public List<Post> findAll() {
        return postRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Post update(Long id, UpdatePostRequest request) {
        Post post = findById(id);

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getCountry() != null) post.setCountry(request.getCountry());
        if (request.getThemes() != null) post.setThemes(request.getThemes());
        if (request.getMood() != null) post.setMood(request.getMood());
        if (request.getImageUrl() != null) post.setImageUrl(request.getImageUrl());
        if (request.getIsAnonymous() != null) post.setIsAnonymous(request.getIsAnonymous());

        return post;
    }

    @Transactional
    public void delete(Long id) {
        postRepository.delete(findById(id));
    }
}
