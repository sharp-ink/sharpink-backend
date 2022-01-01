package io.sharpink.api.resource.story.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a story to be created
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryRequest {
  private String title;
  private boolean originalStory;
  private Long authorId;
}
