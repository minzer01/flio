package kr.adapterz.jpa_practice.entity.user;

import jakarta.persistence.*;
import kr.adapterz.jpa_practice.entity.Comment;
import kr.adapterz.jpa_practice.entity.post.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_email")
    private String email;
    private String password;
    private String nickname;

    @OneToMany(mappedBy = "writer")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "writer")
    private List<Comment> comments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    public void withdraw() { //회원 탈퇴 처리
        this.status = UserStatus.DELETED;
        this.nickname = "탈퇴한 사용자";
        this.email = null;
        this.password = null;
    }

    protected User() {}

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User(String email, String password, String nickname, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
    }

}
