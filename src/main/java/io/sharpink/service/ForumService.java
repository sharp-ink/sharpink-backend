package io.sharpink.service;

import io.sharpink.mapper.forum.MessageMapper;
import io.sharpink.mapper.forum.ThreadMapper;
import io.sharpink.persistence.dao.forum.MessageDao;
import io.sharpink.persistence.dao.forum.ThreadDao;
import io.sharpink.persistence.dao.forum.ThreadSpecification;
import io.sharpink.persistence.dao.story.StoryDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.persistence.entity.forum.MessagesLoadingStrategy;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.rest.dto.request.forum.MessageRequest;
import io.sharpink.rest.dto.request.forum.ThreadRequest;
import io.sharpink.rest.dto.request.forum.search.ThreadSearch;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import io.sharpink.rest.exception.MissingEntity;
import io.sharpink.rest.exception.NotFound404Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.reverseOrder;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Service
public class ForumService {

    private final ThreadDao threadDao;
    private final MessageDao messageDao;
    private final UserDao userDao;
    private final StoryDao storyDao;
    private final ThreadSpecification threadSpecification;
    private final ThreadMapper threadMapper;
    private final MessageMapper messageMapper;

    @Autowired
    public ForumService(ThreadDao threadDao, MessageDao messageDao, UserDao userDao, StoryDao storyDao, ThreadSpecification threadSpecification,
        ThreadMapper threadMapper, MessageMapper messageMapper
    ) {
        this.threadDao = threadDao;
        this.messageDao = messageDao;
        this.userDao = userDao;
        this.storyDao = storyDao;
        this.threadSpecification = threadSpecification;
        this.threadMapper = threadMapper;
        this.messageMapper = messageMapper;
    }

    public List<ThreadResponse> getAllThreads() {
        List<Thread> threads = (List<Thread>) threadDao.findAll();
        threads.sort(reverseOrder());
        return threadMapper.toThreadResponseList(threads);
    }

    /**
     * Search threads by criteria, eventually applying filters and sorting.
     *
     * @param threadSearch The criteria threads should match, including filters and sorting
     * @return a list of threads matching the given criteria, with appropriate filters / sorting
     */
    public List<ThreadResponse> searchThreads(ThreadSearch threadSearch) {
        String title = threadSearch.getCriteria().getTitle();
        String authorName = threadSearch.getCriteria().getAuthorName();
        String keyWords = threadSearch.getCriteria().getKeyWords();

        List<Thread> threads = threadDao.findAll(
            threadSpecification.hasTitleLike(title)
            .and(threadSpecification.hasAuthorLike(authorName))
            .and(threadSpecification.threadContentContainsKeyWords(keyWords)));

        threads.sort(reverseOrder());

        return threadMapper.toThreadResponseList(threads);
    }

    public Optional<ThreadResponse> getThread(Long id) {
        Optional<Thread> optionalThread = threadDao.findById(id);
        return optionalThread.map(thread -> threadMapper.toThreadResponse(thread, MessagesLoadingStrategy.ENABLED));
    }

    public long createThread(ThreadRequest threadRequest) {
        Thread thread = threadMapper.toThread(threadRequest);
        thread.setAuthor(userDao.findById(threadRequest.getOriginalAuthorId()).orElseThrow());
        thread.setCreationDate(LocalDateTime.now());

        thread = threadDao.save(thread);

        if (threadRequest.getStoryId() != null) {
            Story story = storyDao.findById(threadRequest.getStoryId()).orElseThrow();
            story.setThread(thread);
            storyDao.save(story);
        }

        return thread.getId();
    }

    public synchronized void removeThread(Long id) {
        try {
            threadDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFound404Exception(MissingEntity.FORUM_THREAD);
        }
    }

    public synchronized long createMessage(Long threadId, MessageRequest messageRequest) {

        Message message = messageMapper.toMessage(messageRequest);
        message.setAuthor(userDao.findById(messageRequest.getAuthorId()).orElseThrow());
        message.setPublicationDate(LocalDateTime.now());

        Thread thread = threadDao.findById(threadId).orElseThrow();
        message.setThread(thread);

        List<Message> messages = thread.getMessages();
        if (isEmpty(messages)) {
            message.setNumber(1);
        } else {
            message.setNumber(thread.getLastPublishedMessage().getNumber() + 1);
        }

        message = messageDao.save(message);
        return message.getId();
    }

    public synchronized void removeMessage(Long id, int number) {
        Thread thread = threadDao.findById(id).orElseThrow();
        thread.getMessages().removeIf(message -> message.getNumber() == number);
        threadDao.save(thread);
    }
}
