package io.sharpink.api.resource.story.dto;

import lombok.Data;

/**
 * Represents a chapter to be created (added to a story)
 */
@Data
public class ChapterRequest {
  private String title;
  private String content;
}
