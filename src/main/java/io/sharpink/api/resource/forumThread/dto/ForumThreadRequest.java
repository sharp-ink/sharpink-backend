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
public class ForumThreadRequest {

    @NotNull
    private Long originalAuthorId;

    @NotNull
    private String title;

    private Long storyId;

}
