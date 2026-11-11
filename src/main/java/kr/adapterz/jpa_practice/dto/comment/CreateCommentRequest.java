package kr.adapterz.jpa_practice.dto.comment;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long writerId;
    private Long postId;
    private String content;
    private Boolean isAnonymous = false;

    public CreateCommentRequest() {

    }
}
