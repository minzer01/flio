package kr.adapterz.jpa_practice.entity.post;

import jakarta.persistence.*;
import kr.adapterz.jpa_practice.entity.Comment;
import kr.adapterz.jpa_practice.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;

    private String country;

   //theme 다중 선택 가능
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "post_themes",
            joinColumns = @JoinColumn(name = "post_id")
    )
    @Column(name = "theme")
    private List<Theme> themes = new ArrayList<>();


    //mood는 단일 선택
    @Enumerated(EnumType.STRING)
    private Mood mood;

    private Boolean isAnonymous;

    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") //post.user_id -> user.user_id
    private User writer;

    //댓글도 같이 삭제되도록 cascade + orphanRemoval
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    protected Post() {}

    public Post(
            String title,
            String content,
            String country,
            List<Theme> themes,
            Mood mood,
            Boolean isAnonymous,
            User writer
    ) {
        this.title = title;
        this.content = content;
        this.country = country;
        this.themes = themes;
        this.mood = mood;
        this.isAnonymous = isAnonymous;
        setWriter(writer); //양방향 연관관계 때문에 this 사용X
    }

    public void setWriter(User writer) {
        this.writer = writer;
        writer.getPosts().add(this);
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
