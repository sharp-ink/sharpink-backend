package io.sharpink.rest.controller;

import io.sharpink.rest.dto.response.story.StoryResponse;

public class StoryMockUtil {

  static final StoryResponse STORY_RESPONSE_MOCK = mockStoryResponse();

  private static StoryResponse mockStoryResponse() {
    return StoryResponse.builder()
      .id(1L)
      .title("Hello world!")
      .authorId(1L)
      // TODO set more fields if needed
      .build();
  }

}
