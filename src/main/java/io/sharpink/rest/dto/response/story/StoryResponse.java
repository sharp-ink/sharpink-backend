package io.sharpink.rest.dto.response.story;

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
public class StoryResponse {

  private Long id;
  private String title;
  private String type;
  private boolean originalStory;
  private String status;
  private String summary;
  private String thumbnail;
  private boolean published;
  private Long authorId;
  private Integer chaptersNumber;
  private List<ChapterResponse> chapters; // peut être null
  private LocalDateTime creationDate;
  private LocalDateTime lastModificationDate;
  private LocalDateTime finalReleaseDate;
  private Long threadId; // peut être null
}
