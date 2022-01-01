package io.sharpink.api.resource.story.dto;

import io.sharpink.api.resource.story.enums.StoryStatus;
import io.sharpink.api.resource.story.enums.StoryType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoryPatchRequest {
  private String title;
  private StoryType type;
  private boolean originalStory;
  private StoryStatus status;
  private String summary;
  private String thumbnail;
  private Boolean published;
}
