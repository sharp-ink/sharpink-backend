package io.sharpink.service;

import io.sharpink.mapper.forum.MessageMapper;
import io.sharpink.mapper.forum.ThreadMapper;
import io.sharpink.persistence.dao.forum.MessageDao;
import io.sharpink.persistence.dao.forum.ThreadDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.persistence.entity.forum.MessagesLoadingStrategy;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.rest.dto.request.forum.MessageRequest;
import io.sharpink.rest.dto.request.forum.ThreadRequest;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ForumService {

  private ThreadDao threadDao;
  private MessageDao messageDao;
  private UserDao userDao;
  private ThreadMapper threadMapper;
  private MessageMapper messageMapper;

  @Autowired
  public ForumService(ThreadDao threadDao, MessageDao messageDao, UserDao userDao, ThreadMapper threadMapper, MessageMapper messageMapper) {
    this.threadDao = threadDao;
    this.messageDao = messageDao;
    this.userDao = userDao;
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
    return thread.getId();
  }

  public long createMessage(Long threadId, MessageRequest messageRequest) {
    Message message = messageMapper.toMessage(messageRequest);
    message.setThread(threadDao.findById(threadId).get());
    message.setAuthor(userDao.findById(messageRequest.getAuthorId()).get());
    message.setPublicationDate(LocalDateTime.now());

    message = messageDao.save(message);
    return message.getId();
  }
}
