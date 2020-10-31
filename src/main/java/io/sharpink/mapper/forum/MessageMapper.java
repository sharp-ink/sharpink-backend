package io.sharpink.mapper.forum;

import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.rest.dto.response.forum.MessageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {

  public MessageResponse map(Message source) {
    return MessageResponse.builder()
      .id(source.getId())
      .threadId(source.getThread().getId())
      .authorId(source.getAuthor().getId())
      .publicationDate(source.getPublicationDate())
      .content(source.getContent())
      .build();
  }

  public List<MessageResponse> map(List<Message> source) {
    return source.stream().map(this::map).collect(Collectors.toList());
  }

}
