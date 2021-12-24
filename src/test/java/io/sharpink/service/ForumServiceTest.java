package io.sharpink.service;

import io.sharpink.mapper.forum.MessageMapper;
import io.sharpink.mapper.forum.ThreadMapper;
import io.sharpink.persistence.dao.forum.MessageDao;
import io.sharpink.persistence.dao.forum.ThreadDao;
import io.sharpink.persistence.dao.forum.ThreadSpecification;
import io.sharpink.persistence.dao.story.StoryDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.rest.dto.request.forum.search.ThreadSearch;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static java.time.LocalDateTime.now;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(SpringExtension.class)
class ForumServiceTest {

    @MockBean
    ThreadDao threadDaoMock;

    @MockBean
    MessageDao messageDaoMock;

    @MockBean
    UserDao userDaoMock;

    @MockBean
    StoryDao storyDaoMock;

    @Spy
    ThreadSpecification threadSpecification = new ThreadSpecification();

    MessageMapper messageMapper = new MessageMapper();
    ThreadMapper threadMapper = new ThreadMapper(messageMapper);

    ForumService forumService;

    @BeforeEach
    void setUp() {
        forumService = new ForumService(threadDaoMock, messageDaoMock, userDaoMock, storyDaoMock, threadSpecification, threadMapper, messageMapper);
    }

    @DisplayName("Should return the whole list of threads present in DB, sorted by descending publication date")
    @Test
    void getAllThreads() {
        // given
        var randomThreadsList = mockRandomThreadsList();
        int threadsCount = randomThreadsList.size();
        given(threadDaoMock.findAll()).willReturn(randomThreadsList);

        // when
        List<ThreadResponse> threads = forumService.getAllThreads();

        // then
        then(threadDaoMock).should().findAll();
        assertThat(threads).hasSize(threadsCount)
            .isSortedAccordingTo(comparing(ThreadResponse::getCreationDate).reversed());
    }

    @DisplayName("Should return threads matching given criteria")
    @Test
    void searchThreads() {
        // given
        var randomThreadsList = mockRandomThreadsList();
        int threadsCount = randomThreadsList.size();
        String randomTitleCriteria = randomAlphabetic(10);
        String randomAuthorNameCriteria = randomAlphabetic(10);
        String keyWords = "key_word1 key_word2 key_word3";
        given(threadDaoMock.findAll(any(Specification.class))).willReturn(randomThreadsList);

        // when
        ThreadSearch threadSearch = ThreadSearch.builder()
            .criteria(ThreadSearch.Criteria.builder()
                .title(randomTitleCriteria)
                .authorName(randomAuthorNameCriteria)
                .keyWords(keyWords).build())
            .build();
        List<ThreadResponse> threads = forumService.searchThreads(threadSearch);

        // then
        then(threadSpecification).should().hasTitleLike(randomTitleCriteria);
        then(threadSpecification).should().hasAuthorLike(randomAuthorNameCriteria);
        then(threadSpecification).should().threadContentContainsKeyWords(keyWords);
        then(threadDaoMock).should().findAll(any(Specification.class));
        assertThat(threads).hasSize(threadsCount);
    }

    private List<Thread> mockRandomThreadsList() {
        // a list of threads of an arbitrary size between 0 and 10
        return LongStream.range(0, RandomUtils.nextInt(0, 11))
            .mapToObj(this::mockRandomThread)
            .collect(toList());
    }

    private Thread mockRandomThread(long id) {
        var user1 = User.builder().id(1L).build(); // hard-coded user
        return Thread.builder()
            .id(id)
            .author(user1)
            .title(randomAlphabetic(20))
            .creationDate(now())
            .messages(new ArrayList<>())
            .build();
    }

}
