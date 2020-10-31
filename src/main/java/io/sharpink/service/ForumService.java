package io.sharpink.service;

import io.sharpink.mapper.forum.ThreadMapper;
import io.sharpink.persistence.dao.forum.ThreadDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.rest.dto.request.ThreadRequest;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForumService {

  private ThreadDao threadDao;
  private UserDao userDao;
  private ThreadMapper threadMapper;

  @Autowired
  public ForumService(ThreadDao threadDao, UserDao userDao, ThreadMapper threadMapper) {
    this.threadDao = threadDao;
    this.userDao = userDao;
    this.threadMapper = threadMapper;
  }

  public List<ThreadResponse> getAllThreads() {
    List<Thread> threads = (List<Thread>) threadDao.findAll();
    return threadMapper.toThreadResponseList(threads);
  }

  public long createThread(ThreadRequest threadRequest) {
    Thread thread = threadMapper.toThread(threadRequest);

    thread.setOriginalAuthor(userDao.findById(threadRequest.getOriginalAuthorId()).get());

    thread.setCreationDate(LocalDateTime.now());

    thread = threadDao.save(thread);
    return thread.getId();
  }
}
