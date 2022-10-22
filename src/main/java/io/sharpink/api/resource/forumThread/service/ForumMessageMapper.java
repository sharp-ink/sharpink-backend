package io.sharpink.api.resource.forumThread.service;

import io.sharpink.api.resource.forumThread.persistence.ForumMessage;
import io.sharpink.api.resource.forumThread.dto.ForumMessageRequest;
import io.sharpink.api.resource.forumThread.dto.ForumMessageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ForumMessageMapper {

  public ForumMessageResponse toMessageResponse(ForumMessage source) {
    return ForumMessageResponse.builder()
      .id(source.getId())
      .threadId(source.getThread().getId())
      .threadTitle(source.getThread().getTitle())
      .authorId(source.getAuthor().getId())
      .authorNickname(source.getAuthor().getNickname())
      .publicationDate(source.getPublicationDate())
      .number(source.getNumber())
      .content(source.getContent())
      .build();
  }

  public List<ForumMessageResponse> toMessageResponseList(List<ForumMessage> source) {
    return source.stream().map(this::toMessageResponse).collect(Collectors.toList());
  }

  public ForumMessage toMessage(ForumMessageRequest source) {
    return ForumMessage.builder().content(source.getContent()).build();
  }
}
