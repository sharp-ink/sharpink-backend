package io.sharpink.api.resource.forumThread.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumThreadResponse {
    private Long id;
    private Long authorId;
    private String authorNickname;
    private String title;
    @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
    private LocalDateTime creationDate;
    private int messagesCount; // toujours renseigné
    private ForumMessageResponse lastMessage; // toujours renseigné
    private List<ForumMessageResponse> messages; // pas toujours renseigné
}
