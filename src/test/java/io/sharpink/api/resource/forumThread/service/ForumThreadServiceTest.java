package io.sharpink.api.resource.forumThread.service;

import io.sharpink.api.resource.forumThread.dto.ForumThreadResponse;
import io.sharpink.api.resource.forumThread.dto.search.ForumThreadSearch;
import io.sharpink.api.resource.forumThread.persistence.ForumMessageDao;
import io.sharpink.api.resource.forumThread.persistence.ForumThread;
import io.sharpink.api.resource.forumThread.persistence.ForumThreadDao;
import io.sharpink.api.resource.forumThread.persistence.ForumThreadSpecification;
import io.sharpink.api.resource.story.persistence.StoryDao;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.resource.user.persistence.user.User;
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
class ForumThreadServiceTest {

    @MockBean
    ForumThreadDao forumThreadDaoMock;

    @MockBean
    ForumMessageDao forumMessageDaoMock;

    @MockBean
    UserDao userDaoMock;

    @MockBean
    StoryDao storyDaoMock;

    @Spy
    ForumThreadSpecification threadSpecification = new ForumThreadSpecification();

    ForumMessageMapper forumMessageMapper = new ForumMessageMapper();
    ForumThreadMapper forumThreadMapper = new ForumThreadMapper(forumMessageMapper);

    ForumThreadService forumThreadService;

    @BeforeEach
    void setUp() {
        forumThreadService = new ForumThreadService(forumThreadDaoMock, forumMessageDaoMock, userDaoMock, storyDaoMock, threadSpecification, forumThreadMapper, forumMessageMapper);
    }

    @DisplayName("Should return the whole list of threads present in DB, sorted by descending publication date")
    @Test
    void getAllThreads() {
        // given
        var randomThreadsList = mockRandomThreadsList();
        int threadsCount = randomThreadsList.size();
        given(forumThreadDaoMock.findAll()).willReturn(randomThreadsList);

        // when
        List<ForumThreadResponse> threads = forumThreadService.getAllThreads();

        // then
        then(forumThreadDaoMock).should().findAll();
        assertThat(threads).hasSize(threadsCount)
            .isSortedAccordingTo(comparing(ForumThreadResponse::getCreationDate).reversed());
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
        given(forumThreadDaoMock.findAll(any(Specification.class))).willReturn(randomThreadsList);

        // when
        ForumThreadSearch threadSearch = ForumThreadSearch.builder()
            .criteria(ForumThreadSearch.Criteria.builder()
                .title(randomTitleCriteria)
                .authorName(randomAuthorNameCriteria)
                .keyWords(keyWords).build())
            .build();
        List<ForumThreadResponse> threads = forumThreadService.searchThreads(threadSearch);

        // then
        then(threadSpecification).should().hasTitleLike(randomTitleCriteria);
        then(threadSpecification).should().hasAuthorLike(randomAuthorNameCriteria);
        then(threadSpecification).should().threadContentContainsKeyWords(keyWords);
        then(forumThreadDaoMock).should().findAll(any(Specification.class));
        assertThat(threads).hasSize(threadsCount);
    }

    private List<ForumThread> mockRandomThreadsList() {
        // a list of threads of an arbitrary size between 0 and 10
        return LongStream.range(0, RandomUtils.nextInt(0, 11))
            .mapToObj(this::mockRandomThread)
            .collect(toList());
    }

    private ForumThread mockRandomThread(long id) {
        var user1 = User.builder().id(1L).build(); // hard-coded user
        return ForumThread.builder()
            .id(id)
            .author(user1)
            .title(randomAlphabetic(20))
            .creationDate(now())
            .messages(new ArrayList<>())
            .build();
    }

}
