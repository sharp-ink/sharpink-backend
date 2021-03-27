package io.sharpink.rest.dto.request.story.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Criteria {
  private String title;
  private String authorName;
}
