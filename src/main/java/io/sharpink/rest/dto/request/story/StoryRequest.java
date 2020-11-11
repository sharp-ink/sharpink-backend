package io.sharpink.rest.dto.request.story;

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
}
