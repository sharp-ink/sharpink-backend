package io.sharpink.mapper.forum;

import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.rest.dto.request.forum.MessageRequest;
import io.sharpink.rest.dto.response.forum.MessageResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageMapper {

  public MessageResponse toMessageResponse(Message source) {
    return MessageResponse.builder()
      .id(source.getId())
      .threadId(source.getThread().getId())
      .authorId(source.getAuthor().getId())
      .authorNickname(source.getAuthor().getNickname())
      .publicationDate(source.getPublicationDate())
      .content(source.getContent())
      .build();
  }

  public List<MessageResponse> toMessageResponseList(List<Message> source) {
    return source.stream().map(this::toMessageResponse).collect(Collectors.toList());
  }

  public Message toMessage(MessageRequest source) {
    return Message.builder().content(source.getContent()).build();
  }
}
