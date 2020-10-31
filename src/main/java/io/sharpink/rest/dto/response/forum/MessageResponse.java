package io.sharpink.rest.dto.response.forum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
  private Long id;
  private Long threadId;
  private Long authorId;
  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss.SSSSSS")
  private LocalDateTime publicationDate;
  private String content;
}
