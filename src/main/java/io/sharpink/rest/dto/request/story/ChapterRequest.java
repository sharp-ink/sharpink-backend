package io.sharpink.rest.dto.request.story;

import lombok.Data;

@Data
public class ChapterRequest {
  private String title;
  private String content;
}
