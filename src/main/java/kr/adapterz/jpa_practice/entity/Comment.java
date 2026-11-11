package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import kr.adapterz.jpa_practice.entity.post.Post;
import kr.adapterz.jpa_practice.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;
    private Boolean isAnonymous;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id") //comment.user_id -> post.user_id
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") //comment.user_id -> user.user_id
    private User writer;

    protected Comment() {}

    public Comment(String content, Boolean isAnonymous, Post post, User writer){
        this.content = content;
        this.isAnonymous = isAnonymous;
        setPost(post);
        setWriter(writer);
    }


    public void setPost(Post post) {
        this.post = post;
        if(!post.getComments().contains(this)) { //중복 체크
            post.getComments().add(this);
        }
    }

    public void setWriter(User writer) {
        this.writer = writer;
        if (!writer.getComments().contains(this)) {
            writer.getComments().add(this);
        }
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
