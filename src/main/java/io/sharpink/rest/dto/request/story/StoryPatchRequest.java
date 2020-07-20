package io.sharpink.rest.dto.request.story;

import io.sharpink.persistence.entity.story.StoryType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoryPatchRequest {
  private StoryType type;
  private String summary;
  private String thumbnail;
  private Boolean published;
}
