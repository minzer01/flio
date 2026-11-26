package com.flio.dto.comment;

import lombok.Data;

@Data
public class UpdateCommentRequest {
    private String content;
    private Boolean isAnonymous;

    public UpdateCommentRequest() {}
}
