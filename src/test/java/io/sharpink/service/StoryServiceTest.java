package io.sharpink.service;

import io.sharpink.UserMockUtil;
import io.sharpink.config.SharpinkConfiguration;
import io.sharpink.mapper.story.ChapterMapper;
import io.sharpink.mapper.story.StoryMapper;
import io.sharpink.mapper.user.UserMapper;
import io.sharpink.persistence.dao.story.ChapterDao;
import io.sharpink.persistence.dao.story.StoryDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.story.AuthorLoadingStrategy;
import io.sharpink.persistence.entity.story.ChaptersLoadingStrategy;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.rest.controller.StoryMockUtil;
import io.sharpink.rest.dto.request.story.search.StorySearch;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.rest.dto.response.user.UserResponse;
import io.sharpink.service.picture.PictureManagementService;
import io.sharpink.shared.SortType;
import lombok.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static io.sharpink.persistence.dao.story.StoryDao.*;
import static io.sharpink.rest.controller.StoryMockUtil.mockStory;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.jpa.domain.Specification.where;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(SharpinkConfiguration.class)
@TestPropertySource("classpath:application.properties")
class StoryServiceTest {

    @MockBean
    UserDao userDaoMock;

    @MockBean
    StoryDao storyDaoMock;

    @MockBean
    ChapterDao chapterDaoMock;

    ChapterMapper chapterMapper = new ChapterMapper();

    StoryMapper storyMapper = new StoryMapper(chapterMapper);

    UserMapper userMapper = new UserMapper(storyMapper);

    @MockBean
    PictureManagementService pictureManagementServiceMock;

    @Autowired
    SharpinkConfiguration sharpinkConfiguration;

    StoryService storyService;

    @BeforeEach
    public void setUp() {
        storyService = spy(
            new StoryService(userDaoMock, storyDaoMock, chapterDaoMock, storyMapper, userMapper, chapterMapper, pictureManagementServiceMock, sharpinkConfiguration)
        );
    }

    @Test
    @DisplayName("Should return all public stories")
    void getAllPublicStories() {
        // given
        Story story1 = mockStory(1L, "Hello world!", 1);
        story1.setPublished(true);
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(List.of(story1));

        // when
        List<StoryResponse> storyResponseList = storyService.getAllPublicStories(AuthorLoadingStrategy.DISABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        AssertableStoryResponse expectedStoryResponse1 = buildAssertableStoryResponse(storyMapper.toStoryResponse(story1, ChaptersLoadingStrategy.ONLY_FIRST), AuthorLoadingStrategy.DISABLED);
        List<AssertableStoryResponse> assertableStoryResponseList = storyResponseList.stream()
            .map(this::buildAssertableStoryResponse)
            .collect(toList());
        assertThat(assertableStoryResponseList).containsExactly(expectedStoryResponse1);

    }

    @Test
    @DisplayName("Should return 1 story when there is 2 public stories but only one has chapters")
    void getAllPublicStories_1StoryWithChaptersAnd1StoryWithoutChapters() {
        // given
        Story story1 = mockStory(1L, "Hello world!", 10);
        Story story2 = mockStory(2L, "Bye bye world!", 0);
        story1.setPublished(true);
        story2.setPublished(true);
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(List.of(story1, story2));

        // when
        List<StoryResponse> storyResponseList = storyService.getAllPublicStories(AuthorLoadingStrategy.DISABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        AssertableStoryResponse expectedStoryResponse1 = buildAssertableStoryResponse(storyMapper.toStoryResponse(story1, ChaptersLoadingStrategy.ONLY_FIRST), AuthorLoadingStrategy.DISABLED);
        List<AssertableStoryResponse> assertableStoryResponseList = storyResponseList.stream()
            .map(this::buildAssertableStoryResponse)
            .collect(toList());
        assertThat(assertableStoryResponseList).containsExactly(expectedStoryResponse1);

    }

    @Test
    @DisplayName("Field 'StoryResponse.author' should not be set when using AuthorLoadingStrategy.DISABLED")
    void getAllPublicStories_DontLoadAuthor() {
        // given
        Story story1 = mockStory(1L, "Hi buddy!", 2);
        story1.setPublished(true);
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(List.of(story1));

        // when
        List<StoryResponse> storyResponseList = storyService.getAllPublicStories(AuthorLoadingStrategy.DISABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        verifyNoInteractions(userDaoMock);
        AssertableStoryResponse expectedStoryResponse1 = buildAssertableStoryResponse(storyMapper.toStoryResponse(story1, ChaptersLoadingStrategy.ONLY_FIRST), AuthorLoadingStrategy.DISABLED);
        List<AssertableStoryResponse> assertableStoryResponseList = storyResponseList.stream()
            .map(this::buildAssertableStoryResponse)
            .collect(toList());
        assertThat(assertableStoryResponseList).containsExactly(expectedStoryResponse1);
        assertThat(assertableStoryResponseList.get(0).getAuthor()).isNull();
    }

    @Test
    @DisplayName("Field 'StoryResponse.author' should be set when using AuthorLoadingStrategy.ENABLED")
    void getAllPublicStories_LoadAuthor() {
        // given
        Story story1 = mockStory(1L, "Hi buddy!", 2);
        story1.setPublished(true);
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(List.of(story1));
        User userMock = UserMockUtil.USER_MOCK;
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        List<StoryResponse> storyResponseList = storyService.getAllPublicStories(AuthorLoadingStrategy.ENABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        AssertableStoryResponse expectedStoryResponse1 = buildAssertableStoryResponse(storyMapper.toStoryResponse(story1, ChaptersLoadingStrategy.ONLY_FIRST), AuthorLoadingStrategy.ENABLED);
        List<AssertableStoryResponse> assertableStoryResponseList = storyResponseList.stream()
            .map(this::buildAssertableStoryResponse)
            .collect(toList());
        assertThat(assertableStoryResponseList).containsExactly(expectedStoryResponse1);
        assertThat(assertableStoryResponseList.get(0).getAuthor())
            .isNotNull()
            .isEqualTo(AssertableUserResponse.builder().id(userMock.getId()).build());
    }

    @Test
    void searchStories_BasicSearchWithNoSorting() {
        // given
        Story storyMock = StoryMockUtil.getStoryMock();
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(singletonList(storyMock));

        // when
        StorySearch storySearch = StorySearch.builder()
            .criteria(StorySearch.Criteria.builder().title(randomAlphabetic(10)).authorName(randomAlphabetic(10)).build())
            .build();
        List<StoryResponse> storyResponseList = storyService.searchStories(storySearch, AuthorLoadingStrategy.DISABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        verify(storyService, never()).applySorting(anyList(), any(StorySearch.Sort.class));
        assertThat(storyResponseList).hasSize(1);
        StoryResponse expectedStoryResponse = storyMapper.toStoryResponse(storyMock, ChaptersLoadingStrategy.ONLY_FIRST);
        assertThat(storyResponseList.get(0)).isEqualTo(expectedStoryResponse);
    }

    @Test
    void searchStories_BasicSearchSortByTitleAsc() {
        // given
        User userMock = UserMockUtil.USER_MOCK;
        List<Story> storyListMock = asList(
            Story.builder().id(1L).title("A Beautiful Crate").author(userMock).chaptersNumber(0).build(),
            Story.builder().id(2L).title("Great Holiday Inn").author(userMock).chaptersNumber(0).build(),
            Story.builder().id(3L).title("Don't Exist Forever!").author(userMock).chaptersNumber(0).build());
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(storyListMock);

        // when
        StorySearch.Criteria criteria = StorySearch.Criteria.builder().title(randomAlphabetic(10)).authorName(randomAlphabetic(10)).build();
        StorySearch.Sort sort = StorySearch.Sort.builder().title(SortType.ASC).build();
        StorySearch storySearch = StorySearch.builder().criteria(criteria).sort(sort).build();
        List<StoryResponse> storyResponseList = storyService.searchStories(storySearch, AuthorLoadingStrategy.DISABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        verify(storyService).applySorting(storyListMock, sort);
        assertThat(storyResponseList)
            .hasSize(3)
            .isSortedAccordingTo(comparing(StoryResponse::getTitle));
    }

    @Test
    void searchStories_BasicSearchSortByTitleDesc() {
        // given
        User userMock = UserMockUtil.USER_MOCK;
        List<Story> storyListMock = asList(
            Story.builder().id(1L).title("A Beautiful Crate").author(userMock).chaptersNumber(0).build(),
            Story.builder().id(2L).title("Great Holiday Inn").author(userMock).chaptersNumber(0).build(),
            Story.builder().id(3L).title("Don't Exist Forever!").author(userMock).chaptersNumber(0).build());
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(storyListMock);

        // when
        StorySearch.Criteria criteria = StorySearch.Criteria.builder().title(randomAlphabetic(10)).authorName(randomAlphabetic(10)).build();
        StorySearch.Sort sort = StorySearch.Sort.builder().title(SortType.DESC).build();
        StorySearch storySearch = StorySearch.builder().criteria(criteria).sort(sort).build();
        List<StoryResponse> storyResponseList = storyService.searchStories(storySearch, AuthorLoadingStrategy.DISABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        verify(storyService).applySorting(storyListMock, sort);
        assertThat(storyResponseList)
            .hasSize(3)
            .isSortedAccordingTo(comparing(StoryResponse::getTitle).reversed());
    }

    @Test
    void searchStories_BasicSearchSortByAuthorNameAsc() {
        // given
        User userMock1 = User.builder().id(1L).nickname("God").email("the-all-mighty-guy@heaven.io").build();
        User userMock2 = User.builder().id(2L).nickname("Lucifer").email("Kevin666@hell.com").build();
        User userMock3 = User.builder().id(3L).nickname("John Doe").email("jd@earth.fail").build();
        Story storyMock1 = Story.builder().id(1L).title("Aaaaa").author(userMock1).chaptersNumber(0).build();
        Story storyMock2 = Story.builder().id(1L).title("Bbbbb").author(userMock2).chaptersNumber(0).build();
        Story storyMock3 = Story.builder().id(1L).title("Ccccc").author(userMock3).chaptersNumber(0).build();
        List<Story> storyListMock = asList(storyMock1, storyMock2, storyMock3);
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(storyListMock);
        when(userDaoMock.findById(1L)).thenReturn(Optional.of(userMock1));
        when(userDaoMock.findById(2L)).thenReturn(Optional.of(userMock2));
        when(userDaoMock.findById(3L)).thenReturn(Optional.of(userMock3));

        // when
        StorySearch.Criteria criteria = StorySearch.Criteria.builder().title(randomAlphabetic(10)).authorName(randomAlphabetic(10)).build();
        StorySearch.Sort sort = StorySearch.Sort.builder().authorName(SortType.ASC).build();
        StorySearch storySearch = StorySearch.builder().criteria(criteria).sort(sort).build();
        List<StoryResponse> storyResponseList = storyService.searchStories(storySearch, AuthorLoadingStrategy.ENABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        verify(storyService).applySorting(storyListMock, sort);
        assertThat(storyResponseList)
            .hasSize(3)
            .isSortedAccordingTo(comparing(storyResponse -> storyResponse.getAuthor().getNickname()));
    }

    @Test
    void searchStories_BasicSearchSortByAuthorNameDesc() {
        // given
        User userMock1 = User.builder().id(1L).nickname("God").email("the-all-mighty-guy@heaven.io").build();
        User userMock2 = User.builder().id(2L).nickname("Lucifer").email("Kevin666@hell.com").build();
        User userMock3 = User.builder().id(3L).nickname("John Doe").email("jd@earth.fail").build();
        Story storyMock1 = Story.builder().id(1L).title("Aaaaa").author(userMock1).chaptersNumber(0).build();
        Story storyMock2 = Story.builder().id(1L).title("Bbbbb").author(userMock2).chaptersNumber(0).build();
        Story storyMock3 = Story.builder().id(1L).title("Ccccc").author(userMock3).chaptersNumber(0).build();
        List<Story> storyListMock = asList(storyMock1, storyMock2, storyMock3);
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(storyListMock);
        when(userDaoMock.findById(1L)).thenReturn(Optional.of(userMock1));
        when(userDaoMock.findById(2L)).thenReturn(Optional.of(userMock2));
        when(userDaoMock.findById(3L)).thenReturn(Optional.of(userMock3));

        // when
        StorySearch.Criteria criteria = StorySearch.Criteria.builder().title(randomAlphabetic(10)).authorName(randomAlphabetic(10)).build();
        StorySearch.Sort sort = StorySearch.Sort.builder().authorName(SortType.DESC).build();
        StorySearch storySearch = StorySearch.builder().criteria(criteria).sort(sort).build();
        List<StoryResponse> storyResponseList = storyService.searchStories(storySearch, AuthorLoadingStrategy.ENABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        verify(storyService).applySorting(storyListMock, sort);
        assertThat(storyResponseList)
            .hasSize(3)
            .isSortedAccordingTo(comparing((StoryResponse storyResponse) -> storyResponse.getAuthor().getNickname()).reversed());
    }

    @Test
    void searchStories_BasicSearchWithNoSorting_LoadAuthor() {
        // given
        Story storyMock = StoryMockUtil.getStoryMock();
        when(storyDaoMock.findAll(any(Specification.class))).thenReturn(singletonList(storyMock));
        User userMock = UserMockUtil.USER_MOCK;
        when(userDaoMock.findById(anyLong())).thenReturn(Optional.of(userMock));

        // when
        StorySearch storySearch = StorySearch.builder()
            .criteria(StorySearch.Criteria.builder().title(randomAlphabetic(10)).authorName(randomAlphabetic(10)).build())
            .build();
        List<StoryResponse> storyResponseList = storyService.searchStories(storySearch, AuthorLoadingStrategy.ENABLED);

        // then
        verify(storyDaoMock).findAll(any(Specification.class));
        verify(storyService, never()).applySorting(anyList(), any(StorySearch.Sort.class));
        List<AssertableStoryResponse> assertableStoryResponseList = storyResponseList.stream()
            .map(storyResponse -> buildAssertableStoryResponse(storyResponse, AuthorLoadingStrategy.ENABLED))
            .collect(toList());
        AssertableStoryResponse expectedStoryResponse =
            buildAssertableStoryResponse(storyMapper.toStoryResponse(storyMock, ChaptersLoadingStrategy.ONLY_FIRST), AuthorLoadingStrategy.ENABLED);
        assertThat(assertableStoryResponseList).containsExactly(expectedStoryResponse);
    }

    private AssertableStoryResponse buildAssertableStoryResponse(StoryResponse storyResponse, AuthorLoadingStrategy authorLoadingStrategy) {
        AssertableStoryResponse assertableStoryResponse = buildAssertableStoryResponse(storyResponse);
        assertableStoryResponse.setAuthor(
            authorLoadingStrategy == AuthorLoadingStrategy.ENABLED ?
                AssertableUserResponse.builder().id(storyResponse.getAuthorId()).build()
                : null
        );

        return assertableStoryResponse;
    }

    private AssertableStoryResponse buildAssertableStoryResponse(StoryResponse storyResponse) {
        return AssertableStoryResponse.builder()
            .id(storyResponse.getId())
            .title(storyResponse.getTitle())
            .authorId(storyResponse.getAuthorId())
            .author(storyResponse.getAuthor() != null ? buildAssertableUserResponse(storyResponse.getAuthor()) : null)
            .build();
    }

    private AssertableUserResponse buildAssertableUserResponse(UserResponse userResponse) {
        return AssertableUserResponse.builder().id(userResponse.getId()).build();
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AssertableUserResponse {
        Long id;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AssertableStoryResponse {
        Long id;
        Long authorId;
        AssertableUserResponse author;
        String title;
    }

}
