package kr.adapterz.jpa_practice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/posts") //http://localhost:8080/posts
    public String postsPage() { // templates/posts.html
        return "posts";
    }

    @GetMapping("/posts/new")
    public String newPostPage() {
        return "post-new";
    }

    @GetMapping("/posts/{postId}") //게시글 목록에서 {postId} 누르면
    public String postDetailPage(@PathVariable Long postId) {
        return "post-detail";
    }

    @GetMapping("/posts/{postId}/edit")
    public String editPostPage(@PathVariable Long postId) {
        // 새 글 폼과 같은 템플릿 재사용
        return "post-edit";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/members/edit")
    public String memberEditPage() {
        return "member-edit";
    }

    @GetMapping("/members/password")
    public String passwordEditPage() {
        return "password-edit";
    }

}
