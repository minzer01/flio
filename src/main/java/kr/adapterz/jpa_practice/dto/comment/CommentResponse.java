package kr.adapterz.jpa_practice.dto.comment;

import kr.adapterz.jpa_practice.entity.Comment;

import kr.adapterz.jpa_practice.entity.user.User;
import kr.adapterz.jpa_practice.entity.user.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String content;

    private Boolean isAnonymous;
    private int likeCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long postId;
    private Long writerId;
    private String writerNickname;

    public CommentResponse(
            Long id,
            String content,
            Boolean isAnonymous,
            Long postId,
            Long writerId,
            String writerNickname,
            int likeCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.postId = postId;
        this.writerId = writerId;
        this.writerNickname = writerNickname;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static CommentResponse of(Comment comment) {
        User writer = comment.getWriter();
        String nickname;

        if (writer.getStatus() == UserStatus.DELETED) {
            nickname = "탈퇴한 사용자";
        } else if (comment.getIsAnonymous()) {
            nickname = "익명";
        } else {
            nickname = writer.getNickname();
        }

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getIsAnonymous(),
                comment.getPost().getId(),
                comment.getWriter().getId(),
                nickname,
                comment.getLikeCount(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
