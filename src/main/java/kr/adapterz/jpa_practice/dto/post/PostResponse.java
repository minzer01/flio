package kr.adapterz.jpa_practice.dto.post;

import kr.adapterz.jpa_practice.entity.post.Post;
import kr.adapterz.jpa_practice.entity.post.Theme;
import kr.adapterz.jpa_practice.entity.post.Mood;
import kr.adapterz.jpa_practice.entity.user.User;
import kr.adapterz.jpa_practice.entity.user.UserStatus;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String content;

    private String country;
    private List<Theme> themes;
    private Mood mood;

    private Boolean isAnonymous;
    private String imageUrl;
    private int likeCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long writerId;
    private String writerNickname;

    public PostResponse(
            Long id,
            String title,
            String content,
            String country,
            List<Theme> themes,
            Mood mood,
            Boolean isAnonymous,
            String imageUrl,
            int likeCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long writerId,
            String writerNickname
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.country = country;
        this.themes = themes;
        this.mood = mood;
        this.isAnonymous = isAnonymous;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
    }
    public static PostResponse of(Post post) {
        User writer = post.getWriter();
        String nickname;

        if (writer.getStatus() == UserStatus.DELETED) {
            nickname = "탈퇴한 사용자";
        } else if (post.getIsAnonymous()) {
            nickname = "익명";
        } else {
            nickname = writer.getNickname();
        }

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCountry(),
                post.getThemes(),
                post.getMood(),
                post.getIsAnonymous(),
                post.getImageUrl(),
                post.getLikeCount(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                writer.getId(),
                nickname
        );
    }
}
