package io.sharpink.api.resource.story;

import io.sharpink.SharpInkBackendApplication;
import io.sharpink.api.resource.story.dto.StoryResponse;
import io.sharpink.api.resource.story.dto.search.StorySearch;
import io.sharpink.api.resource.story.persistence.Story;
import io.sharpink.api.resource.story.persistence.StoryDao;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.util.JsonUtil;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = SharpInkBackendApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserDao userDao;
    @Autowired private StoryDao storyDao;

    private final StorySearch.Criteria EMPTY_CRITERIA_WITHOUT_FILTER_AND_SORTING = StorySearch.Criteria.builder()
        .title(null)
        .authorName(null)
        .build();

    User toto;
    User titi;

    Story secretStory;
    Story superSecretStory;
    Story loveStory;

    @BeforeAll
    void init() {
        toto = User.builder().nickname("Toto").email("toto@toto.com").password("Toto123").registrationDate(now()).build();
        titi = User.builder().nickname("Titi").email("titi@titi.com").password("Titi456").registrationDate(now()).build();

        userDao.saveAll(asList(toto, titi));

        secretStory = Story.builder()
            .author(toto)
            .title("This is a secret story")
            .published(true)
            .lastModificationDate(LocalDateTime.now())
            .build();

        superSecretStory = Story.builder()
            .author(toto)
            .title("This story is way more secret than the other")
            .published(true)
            .lastModificationDate(LocalDateTime.now())
            .build();

        loveStory = Story.builder()
            .author(titi)
            .title("A story about love")
            .published(true)
            .lastModificationDate(LocalDateTime.now())
            .build();

        storyDao.saveAll(asList(secretStory, superSecretStory, loveStory));
    }

    @Test
    @DisplayName("All stories should be returned when searching with empty criteria")
    public void search_EmptyCriteria() throws Exception {
        // when
        StorySearch storySearch = StorySearch.builder().criteria(EMPTY_CRITERIA_WITHOUT_FILTER_AND_SORTING).build();
        //@formatter:off
        String jsonResult = mockMvc.perform(
                post("/stories/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(storySearch)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        //@formatter:on

        // then
        List<StoryResponse> storyResponses = JsonUtil.fromJsonArray(jsonResult, StoryResponse.class);
        assertThat(storyResponses).hasSize(3);
        AssertableStory expectedSecretStory = buildAssertableStory(secretStory);
        AssertableStory expectedSuperSecretStory = buildAssertableStory(superSecretStory);
        AssertableStory expectedLoveStory = buildAssertableStory(loveStory);
        AssertableStory story1 = buildAssertableStory(storyResponses.get(0));
        AssertableStory story2 = buildAssertableStory(storyResponses.get(1));
        AssertableStory story3 = buildAssertableStory(storyResponses.get(2));
        assertThat(story1).isEqualTo(expectedSecretStory);
        assertThat(story2).isEqualTo(expectedSuperSecretStory);
        assertThat(story3).isEqualTo(expectedLoveStory);
    }

    @Test
    @DisplayName("Should return 1 story when searching with title 'This is a secret story'")
    public void search_TitleOK() throws Exception {
        // when
        //@formatter:off
        StorySearch storySearch = StorySearch.builder().criteria(
            StorySearch.Criteria.builder()
                .title("This is a secret story")
                .authorName(null)
                .build()
        ).build();

        String jsonResult = mockMvc.perform(
                post("/stories/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(storySearch)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        //@formatter:on

        // then
        List<StoryResponse> storyResponses = JsonUtil.fromJsonArray(jsonResult, StoryResponse.class);
        assertThat(storyResponses).hasSize(1);
        AssertableStory expectedSecretStory = buildAssertableStory(secretStory);
        AssertableStory story = buildAssertableStory(storyResponses.get(0));
        assertThat(story).isEqualTo(expectedSecretStory);
    }

    @Test
    @DisplayName("Should return 1 story when searching with author name 'Titi'")
    public void search_AuthorNameOK() throws Exception {
        // when
        //@formatter:off
        StorySearch storySearch = StorySearch.builder().criteria(
            StorySearch.Criteria.builder()
                .title(null)
                .authorName("Titi")
                .build()
        ).build();

        String jsonResult = mockMvc.perform(
                post("/stories/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(storySearch)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        //@formatter:on

        // then
        List<StoryResponse> storyResponses = JsonUtil.fromJsonArray(jsonResult, StoryResponse.class);
        assertThat(storyResponses).hasSize(1);
        AssertableStory expectedSecretStory = buildAssertableStory(loveStory);
        AssertableStory story = buildAssertableStory(storyResponses.get(0));
        assertThat(story).isEqualTo(expectedSecretStory);
    }

    @Test
    @DisplayName("Should return 1 story when searching with title 'This story is way more secret than the other' and author name 'Toto'")
    public void search_TitleAndAuthorNameOK() throws Exception {
        // when
        //@formatter:off
        StorySearch storySearch = StorySearch.builder().criteria(
            StorySearch.Criteria.builder()
                .title("This story is way more secret than the other")
                .authorName("Toto")
                .build()
        ).build();

        String jsonResult = mockMvc.perform(
                post("/stories/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(storySearch)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        //@formatter:on

        // then
        List<StoryResponse> storyResponses = JsonUtil.fromJsonArray(jsonResult, StoryResponse.class);
        assertThat(storyResponses).hasSize(1);
        AssertableStory expectedSecretStory = buildAssertableStory(superSecretStory);
        AssertableStory story = buildAssertableStory(storyResponses.get(0));
        assertThat(story).isEqualTo(expectedSecretStory);
    }

    @Test
    @DisplayName("No story should be found when searching with title 'This title does not exist' and author name 'Toto'")
    public void search_TitleKOAndAuthorNameOK() throws Exception {
        // when
        //@formatter:off
        StorySearch storySearch = StorySearch.builder().criteria(
            StorySearch.Criteria.builder()
                .title("This title does not exist")
                .authorName("Toto")
                .build()
        ).build();

        String jsonResult = mockMvc.perform(
                post("/stories/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(storySearch)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        //@formatter:on

        // then
        List<StoryResponse> storyResponses = JsonUtil.fromJsonArray(jsonResult, StoryResponse.class);
        assertThat(storyResponses).isEmpty();
    }

    @Test
    @DisplayName("No story should be found when searching with title 'This is a secret story' and author name 'Juju'")
    public void search_TitleOKAndAuthorNameKO() throws Exception {
        // when
        //@formatter:off
        StorySearch storySearch = StorySearch.builder().criteria(
            StorySearch.Criteria.builder()
                .title("This is a secret story")
                .authorName("Juju")
                .build()
        ).build();

        String jsonResult = mockMvc.perform(
                post("/stories/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(storySearch)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        //@formatter:on

        // then
        List<StoryResponse> storyResponses = JsonUtil.fromJsonArray(jsonResult, StoryResponse.class);
        assertThat(storyResponses).isEmpty();
    }

    @AfterAll
    void tearDown() {
        userDao.deleteAll();
        storyDao.deleteAll();
    }

    private AssertableStory buildAssertableStory(Story story) {
        return AssertableStory.builder()
            .id(story.getId())
            .author(AssertableUser.builder().id(story.getAuthor().getId()).build())
            .title(story.getTitle())
            .build();
    }

    private AssertableStory buildAssertableStory(StoryResponse storyResponse) {
        return AssertableStory.builder()
            .id(storyResponse.getId())
            .author(AssertableUser.builder().id(storyResponse.getAuthorId()).build())
            .title(storyResponse.getTitle())
            .build();
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AssertableUser {
        Long id;
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    private static class AssertableStory {
        Long id;
        AssertableUser author;
        String title;
    }
}
