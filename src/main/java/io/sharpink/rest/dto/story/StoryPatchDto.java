package io.sharpink.rest.dto.story;

import io.sharpink.persistence.entity.story.EnumStoryType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoryPatchDto {
  private EnumStoryType type;
  private String summary;
  private String thumbnail;
  private Boolean published;
}
