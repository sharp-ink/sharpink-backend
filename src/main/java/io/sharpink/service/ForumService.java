package io.sharpink.service;

import io.sharpink.mapper.forum.MessageMapper;
import io.sharpink.mapper.forum.ThreadMapper;
import io.sharpink.persistence.dao.forum.MessageDao;
import io.sharpink.persistence.dao.forum.ThreadDao;
import io.sharpink.persistence.dao.story.StoryDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.persistence.entity.forum.MessagesLoadingStrategy;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.rest.dto.request.forum.MessageRequest;
import io.sharpink.rest.dto.request.forum.ThreadRequest;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Service
public class ForumService {

  private ThreadDao threadDao;
  private MessageDao messageDao;
  private UserDao userDao;
  private StoryDao storyDao;
  private ThreadMapper threadMapper;
  private MessageMapper messageMapper;

  @Autowired
  public ForumService(ThreadDao threadDao, MessageDao messageDao, UserDao userDao, StoryDao storyDao, ThreadMapper threadMapper, MessageMapper messageMapper) {
    this.threadDao = threadDao;
    this.messageDao = messageDao;
    this.userDao = userDao;
    this.storyDao = storyDao;
    this.threadMapper = threadMapper;
    this.messageMapper = messageMapper;
  }

  public List<ThreadResponse> getAllThreads() {
    List<Thread> threads = (List<Thread>) threadDao.findAll();
    Collections.sort(threads, Collections.reverseOrder());
    return threadMapper.toThreadResponseList(threads);
  }

  public Optional<ThreadResponse> getThread(Long id) {
    Optional<Thread> optionalThread = threadDao.findById(id);
    if (optionalThread.isPresent()) {
      return Optional.of(threadMapper.toThreadResponse(optionalThread.get(), MessagesLoadingStrategy.ENABLED));
    } else {
      return Optional.empty();
    }
  }

  public long createThread(ThreadRequest threadRequest) {
    Thread thread = threadMapper.toThread(threadRequest);
    thread.setAuthor(userDao.findById(threadRequest.getOriginalAuthorId()).get());
    thread.setCreationDate(LocalDateTime.now());

    thread = threadDao.save(thread);

    if (threadRequest.getStoryId() != null) {
      Story story = storyDao.findById(threadRequest.getStoryId()).get();
      story.setThread(thread);
      storyDao.save(story);
    }

    return thread.getId();
  }

  public synchronized long createMessage(Long threadId, MessageRequest messageRequest) {

    Message message = messageMapper.toMessage(messageRequest);
    message.setAuthor(userDao.findById(messageRequest.getAuthorId()).get());
    message.setPublicationDate(LocalDateTime.now());

    Thread thread = threadDao.findById(threadId).get();
    message.setThread(thread);

    List<Message> messages = thread.getMessages();
    if (isNotEmpty(messages)) {
      Collections.sort(messages, Collections.reverseOrder());
      message.setNumber(messages.get(0).getNumber() + 1);
    } else {
      message.setNumber(1);
    }

    message = messageDao.save(message);
    return message.getId();
  }

  public synchronized void removeMessage(Long id, int number) {
    Thread thread = threadDao.findById(id).get();
    thread.getMessages().removeIf(message -> message.getNumber() == number);
    threadDao.save(thread);
  }
}
