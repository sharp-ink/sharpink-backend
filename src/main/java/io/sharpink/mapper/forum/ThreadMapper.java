package io.sharpink.mapper.forum;

import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.rest.dto.request.ThreadRequest;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Component
public class ThreadMapper {

  private MessageMapper messageMapper;

  @Autowired
  public ThreadMapper(MessageMapper messageMapper) {
    this.messageMapper = messageMapper;
  }

  public ThreadResponse toThreadResponse(Thread source) {
    ThreadResponse target = ThreadResponse.builder()
      .id(source.getId())
      .title(source.getTitle())
      .authorId(source.getAuthor().getId())
      .authorNickname(source.getAuthor().getNickname())
      .creationDate(source.getCreationDate())
      .messagesCount(source.getMessages().size())
      .build();

    if (isNotEmpty(source.getMessages())) {
      List<Message> messages = source.getMessages();
      Collections.sort(messages, Collections.reverseOrder());
      Message lastMessage = messages.get(messages.size() - 1);
      target.setLastMessage(messageMapper.map(lastMessage));
    }

    return target;
  }

  public List<ThreadResponse> toThreadResponseList(List<Thread> source) {
    return source.stream().map(this::toThreadResponse).collect(Collectors.toList());
  }

  public Thread toThread(ThreadRequest threadRequest) {
    return Thread.builder().title(threadRequest.getTitle()).build();
  }
}
