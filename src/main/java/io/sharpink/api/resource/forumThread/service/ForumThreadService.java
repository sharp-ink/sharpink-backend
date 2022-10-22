package io.sharpink.api.resource.forumThread.service;

import io.sharpink.api.resource.forumThread.dto.ForumMessageResponse;
import io.sharpink.api.resource.story.persistence.Story;
import io.sharpink.api.resource.story.persistence.StoryDao;
import io.sharpink.api.resource.forumThread.dto.ForumMessageRequest;
import io.sharpink.api.resource.forumThread.dto.ForumThreadRequest;
import io.sharpink.api.resource.forumThread.persistence.ForumMessage;
import io.sharpink.api.resource.forumThread.persistence.ForumThread;
import io.sharpink.api.resource.forumThread.persistence.MessagesLoadingStrategy;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.resource.forumThread.persistence.ForumMessageDao;
import io.sharpink.api.resource.forumThread.persistence.ForumThreadDao;
import io.sharpink.api.resource.forumThread.persistence.ForumThreadSpecification;
import io.sharpink.api.resource.forumThread.dto.search.ForumThreadSearch;
import io.sharpink.api.resource.forumThread.dto.ForumThreadResponse;
import io.sharpink.api.shared.exception.MissingEntity;
import io.sharpink.api.shared.exception.NotFound404Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.reverseOrder;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Service
public class ForumThreadService {

    private final ForumThreadDao forumThreadDao;
    private final ForumMessageDao forumMessageDao;
    private final UserDao userDao;
    private final StoryDao storyDao;
    private final ForumThreadSpecification threadSpecification;
    private final ForumThreadMapper forumThreadMapper;
    private final ForumMessageMapper forumMessageMapper;

    @Autowired
    public ForumThreadService(ForumThreadDao forumThreadDao, ForumMessageDao forumMessageDao, UserDao userDao, StoryDao storyDao, ForumThreadSpecification threadSpecification,
                              ForumThreadMapper forumThreadMapper, ForumMessageMapper forumMessageMapper
    ) {
        this.forumThreadDao = forumThreadDao;
        this.forumMessageDao = forumMessageDao;
        this.userDao = userDao;
        this.storyDao = storyDao;
        this.threadSpecification = threadSpecification;
        this.forumThreadMapper = forumThreadMapper;
        this.forumMessageMapper = forumMessageMapper;
    }

    public List<ForumThreadResponse> getAllThreads() {
        List<ForumThread> threads = (List<ForumThread>) forumThreadDao.findAll();
        threads.sort(reverseOrder());
        return forumThreadMapper.toThreadResponseList(threads);
    }

    /**
     * Search threads by criteria, eventually applying filters and sorting.
     *
     * @param threadSearch The criteria threads should match, including filters and sorting
     * @return a list of threads matching the given criteria, with appropriate filters / sorting
     */
    public List<ForumThreadResponse> searchThreads(ForumThreadSearch threadSearch) {
        String title = threadSearch.getCriteria().getTitle();
        String authorName = threadSearch.getCriteria().getAuthorName();
        String keyWords = threadSearch.getCriteria().getKeyWords();

        List<ForumThread> threads = forumThreadDao.findAll(
            threadSpecification.hasTitleLike(title)
            .and(threadSpecification.hasAuthorLike(authorName))
            .and(threadSpecification.threadContentContainsKeyWords(keyWords)));

        threads.sort(reverseOrder());

        return forumThreadMapper.toThreadResponseList(threads);
    }

    public Optional<ForumThreadResponse> getThread(Long id) {
        Optional<ForumThread> optionalThread = forumThreadDao.findById(id);
        return optionalThread.map(thread -> forumThreadMapper.toThreadResponse(thread, MessagesLoadingStrategy.ENABLED));
    }

    public long createThread(ForumThreadRequest forumThreadRequest) {
        ForumThread thread = forumThreadMapper.toThread(forumThreadRequest);
        thread.setAuthor(userDao.findById(forumThreadRequest.getOriginalAuthorId()).orElseThrow());
        thread.setCreationDate(LocalDateTime.now());

        thread = forumThreadDao.save(thread);

        if (forumThreadRequest.getStoryId() != null) {
            Story story = storyDao.findById(forumThreadRequest.getStoryId()).orElseThrow();
            story.setThread(thread);
            storyDao.save(story);
        }

        return thread.getId();
    }

    public synchronized void removeThread(Long id) {
        try {
            forumThreadDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFound404Exception(MissingEntity.FORUM_THREAD);
        }
    }

    public synchronized long createMessage(Long threadId, ForumMessageRequest forumMessageRequest) {

        ForumMessage message = forumMessageMapper.toMessage(forumMessageRequest);
        message.setAuthor(userDao.findById(forumMessageRequest.getAuthorId()).orElseThrow());
        message.setPublicationDate(LocalDateTime.now());

        ForumThread thread = forumThreadDao.findById(threadId).orElseThrow();
        message.setThread(thread);

        List<ForumMessage> messages = thread.getMessages();
        if (isEmpty(messages)) {
            message.setNumber(1);
        } else {
            message.setNumber(thread.getLastPublishedMessage().getNumber() + 1);
        }

        message = forumMessageDao.save(message);
        return message.getId();
    }

    public synchronized void removeMessage(Long id, int number) {
        ForumThread thread = forumThreadDao.findById(id).orElseThrow();
        thread.getMessages().removeIf(message -> message.getNumber() == number);
        forumThreadDao.save(thread);
    }

    public ForumMessageResponse getLastPublishedMessage() {
        var lastPublishedMessage = forumMessageDao.findFirstByOrderByPublicationDateDesc();
        return forumMessageMapper.toMessageResponse(lastPublishedMessage);
    }
}
