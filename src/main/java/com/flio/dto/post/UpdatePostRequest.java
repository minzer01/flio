package com.flio.dto.post;
import com.flio.entity.post.Theme;
import com.flio.entity.post.Mood;

import lombok.Data;
import java.util.List;

@Data
public class UpdatePostRequest {
    private String title;
    private String content;

    private String country;
    private List<Theme> themes;
    private Mood mood;

    private String imageUrl;

    private Boolean isAnonymous;

    public UpdatePostRequest() {

    }

}
