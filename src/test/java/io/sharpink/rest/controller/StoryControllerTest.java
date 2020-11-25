package io.sharpink.rest.controller;

import io.sharpink.SharpInkBackendApplication;
import io.sharpink.persistence.dao.story.StoryDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.story.Story;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.rest.dto.request.story.search.Criteria;
import io.sharpink.rest.dto.request.story.search.StorySearch;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.util.json.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

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

  private final Criteria EMPTY_CRITERIA_WITHOUT_FILTER_AND_SORTING = Criteria.builder()
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
    toto = User.builder().nickname("Toto").build();
    titi = User.builder().nickname("Titi").build();

    asList(toto, titi).forEach(userDao::save);

    secretStory = Story.builder()
      .author(toto)
      .title("This is a secret story")
      .lastModificationDate(LocalDateTime.now())
      .build();

    superSecretStory = Story.builder()
      .author(toto)
      .title("This story is way more secret than the other")
      .lastModificationDate(LocalDateTime.now())
      .build();

    loveStory = Story.builder()
      .author(titi)
      .title("A story about love")
      .lastModificationDate(LocalDateTime.now())
      .build();

    asList(secretStory, superSecretStory, loveStory).forEach(storyDao::save);
  }

  @AfterAll
  void tearDown() {
    userDao.deleteAll();
    storyDao.deleteAll();
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
    assertThat(storyResponses.size()).isEqualTo(3);
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
  public void search_withTitleOK() throws Exception {
    // when
    //@formatter:off
    StorySearch storySearch = StorySearch.builder().criteria(
      Criteria.builder()
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
    assertThat(storyResponses.size()).isOne();
    AssertableStory expectedSecretStory = buildAssertableStory(secretStory);
    AssertableStory story = buildAssertableStory(storyResponses.get(0));
    assertThat(story).isEqualTo(expectedSecretStory);
  }

  @Test
  @DisplayName("Should return 1 story when searching with author name 'Titi'")
  public void search_withAuthorNameOK() throws Exception {
    // when
    //@formatter:off
    StorySearch storySearch = StorySearch.builder().criteria(
      Criteria.builder()
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
    assertThat(storyResponses.size()).isOne();
    AssertableStory expectedSecretStory = buildAssertableStory(loveStory);
    AssertableStory story = buildAssertableStory(storyResponses.get(0));
    assertThat(story).isEqualTo(expectedSecretStory);
  }

  @Test
  @DisplayName("Should return 1 story when searching with title 'This story is way more secret than the other' and author name 'Toto'")
  public void search_withTitleAndAuthorNameOK() throws Exception {
    // when
    //@formatter:off
    StorySearch storySearch = StorySearch.builder().criteria(
      Criteria.builder()
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
    assertThat(storyResponses.size()).isOne();
    AssertableStory expectedSecretStory = buildAssertableStory(superSecretStory);
    AssertableStory story = buildAssertableStory(storyResponses.get(0));
    assertThat(story).isEqualTo(expectedSecretStory);
  }

  @Test
  @DisplayName("No story should be found when searching with title 'This title does not exist' and author name 'Toto'")
  public void search_withTitleKOAndAuthorNameOK() throws Exception {
    // when
    //@formatter:off
    StorySearch storySearch = StorySearch.builder().criteria(
      Criteria.builder()
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
    assertThat(storyResponses.size()).isZero();
  }

  @Test
  @DisplayName("No story should be found when searching with title 'This is a secret story' and author name 'Juju'")
  public void search_withTitleOKAndAuthorNameKO() throws Exception {
    // when
    //@formatter:off
    StorySearch storySearch = StorySearch.builder().criteria(
      Criteria.builder()
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
    assertThat(storyResponses.size()).isZero();
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
