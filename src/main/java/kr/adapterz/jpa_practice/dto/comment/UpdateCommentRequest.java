package kr.adapterz.jpa_practice.dto.comment;

import lombok.Data;

@Data
public class UpdateCommentRequest {
    private String content;
    private Boolean isAnonymous;

    public UpdateCommentRequest() {}
}
