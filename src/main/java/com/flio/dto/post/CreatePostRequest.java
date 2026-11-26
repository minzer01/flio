package com.flio.dto.post;

import com.flio.entity.post.Mood;
import com.flio.entity.post.Theme;

import lombok.Data;
import java.util.List;

@Data
public class CreatePostRequest {
    private String title;
    private String content;

    private String country;
    private List<Theme> themes;
    private Mood mood;

    private String imageUrl;

    private Boolean isAnonymous = false;

    public CreatePostRequest() {}
}


