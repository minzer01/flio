package com.flio.dto.comment;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long postId;
    private String content;
    private Boolean isAnonymous = false;

    public CreateCommentRequest() {

    }
}
