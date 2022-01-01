package io.sharpink.api.resource.forumThread.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumMessageRequest {

    @NotNull
    private Long authorId;

    @NotNull
    private String content;

}
