package io.sharpink.rest.dto.request.story;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryRequest {
  private String title;
  private String type;
  private boolean originalStory;
  private String status;
  private String summary;
  private boolean published;
  private Long authorId;
  private Integer chaptersNumber;
  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
  private LocalDateTime creationDate;
  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
  private LocalDateTime lastModificationDate;
  @JsonFormat(pattern = "yyyyMMdd HH:mm:ss")
  private LocalDateTime finalReleaseDate;
}
