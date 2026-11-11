package kr.adapterz.jpa_practice;

import kr.adapterz.jpa_practice.dto.post.CreatePostRequest;
import kr.adapterz.jpa_practice.entity.post.Mood;
import kr.adapterz.jpa_practice.entity.post.Theme;
import kr.adapterz.jpa_practice.entity.user.User;
import kr.adapterz.jpa_practice.service.PostService;
import kr.adapterz.jpa_practice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class InitSampleDataTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Test
    void initSampleData() {
        // 1. 유저 두 명 생성
        User user1 = userService.create(
                "alice@example.com",
                "password1",
                "Alice"
        );

        User user2 = userService.create(
                "bob@example.com",
                "password2",
                "Bob"
        );

        // 2. user1이 쓴 게시글 두 개
        CreatePostRequest p1 = new CreatePostRequest();
        p1.setWriterId(user1.getId());
        p1.setTitle("Hidden Ramen Shop in Tokyo");
        p1.setContent("A hidden local ramen shop in Shibuya. Super cozy and authentic!");
        p1.setCountry("Japan");
        p1.setMood(Mood.LOCAL);
        p1.setThemes(Arrays.asList(Theme.FOOD, Theme.PHOTO));
        p1.setIsAnonymous(false);
        p1.setImageUrl("https://i.ifh.cc/vJ6Q3r.jpg");
        postService.create(p1);

        CreatePostRequest p2 = new CreatePostRequest();
        p2.setWriterId(user1.getId());
        p2.setTitle("Study Cafe in Seoul");
        p2.setContent("Nice quiet cafe in Hongdae, perfect for coding with a latte.");
        p2.setCountry("South Korea");
        p2.setMood(Mood.QUIET);
        p2.setThemes(Arrays.asList(Theme.FOOD));
        p2.setIsAnonymous(true);   // 익명 글
        postService.create(p2);

        // 3. user2가 쓴 게시글 한 개
        CreatePostRequest p3 = new CreatePostRequest();
        p3.setWriterId(user2.getId());
        p3.setTitle("Sunset at Santorini");
        p3.setContent("The most peaceful sunset I've ever seen. Pure magic.");
        p3.setCountry("Greece");
        p3.setMood(Mood.QUIET);
        p3.setThemes(Arrays.asList(Theme.PHOTO, Theme.ACTIVITY));
        p3.setIsAnonymous(false);
        p3.setImageUrl("https://i.ifh.cc/V9RFQR.webp");


        postService.create(p3);
    }
}
