package com.flio.service;

import com.flio.dto.post.CreatePostRequest;
import com.flio.dto.post.UpdatePostRequest;
import com.flio.entity.post.Mood;
import com.flio.entity.post.Post;
import com.flio.entity.post.Theme;
import com.flio.entity.user.User;
import com.flio.entity.user.UserRole;
import com.flio.repository.PostRepository;
import com.flio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void 게시글_작성자는_인증된_사용자로_저장된다() {
        User writer = user(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(writer));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

        CreatePostRequest request = new CreatePostRequest();
        request.setTitle("제목");
        request.setContent("내용");
        request.setThemes(List.of(Theme.FOOD));
        request.setMood(Mood.LOCAL);

        Post saved = postService.create(request, 1L);

        assertThat(saved.getWriter()).isSameAs(writer);
    }

    @Test
    void 다른_사용자의_게시글은_수정할_수_없다() {
        Post post = post(1L, user(1L));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("바뀐 제목");

        assertThatThrownBy(() -> postService.update(1L, request, 2L))
                .isInstanceOf(SecurityException.class);
        assertThat(post.getTitle()).isEqualTo("원래 제목");
    }

    @Test
    void 작성자는_게시글을_수정할_수_있다() {
        Post post = post(1L, user(1L));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        UpdatePostRequest request = new UpdatePostRequest();
        request.setTitle("바뀐 제목");

        postService.update(1L, request, 1L);

        assertThat(post.getTitle()).isEqualTo("바뀐 제목");
    }

    @Test
    void 다른_사용자의_게시글은_삭제할_수_없다() {
        Post post = post(1L, user(1L));
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.delete(1L, 2L))
                .isInstanceOf(SecurityException.class);
        verify(postRepository, never()).delete(any());
    }

    private User user(Long id) {
        User user = new User("user" + id + "@example.com", "encoded", "user" + id, UserRole.USER);
        user.setId(id);
        return user;
    }

    private Post post(Long id, User writer) {
        Post post = new Post("원래 제목", "내용", "Japan", List.of(Theme.FOOD), Mood.LOCAL, false, writer);
        post.setId(id);
        return post;
    }
}
