package io.sharpink.api.resource.forumThread;

import io.sharpink.SharpInkBackendApplication;
import io.sharpink.api.resource.forumThread.dto.ForumThreadResponse;
import io.sharpink.api.resource.forumThread.dto.search.ForumThreadSearch;
import io.sharpink.api.resource.forumThread.persistence.ForumThread;
import io.sharpink.api.resource.forumThread.persistence.ForumThreadDao;
import io.sharpink.api.resource.user.persistence.UserDao;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.util.JsonUtil;
import lombok.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = SharpInkBackendApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ForumThreadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ForumThreadDao forumThreadDao;

    User shakira;
    ForumThread thread_HipsDontLie;
    ForumThread thread_WakaWaka;
    ForumThread thread_Loca;

    private final ForumThreadSearch EMPTY_CRITERIA_WITHOUT_FILTER_AND_SORTING = ForumThreadSearch.builder()
            .criteria(ForumThreadSearch.Criteria.builder()
                    .title(null)
                    .authorName(null)
                    .keyWords(null).build())
            .filter(null)
            .sort(null).build();

    @BeforeAll
    void init() {
        shakira = User.builder().nickname("Shakira Isabel Mebarak Ripoll").email("shakira@shakira.co").password("Contraseña123")
                .registrationDate(LocalDateTime.now()).build();
        userDao.save(shakira);

        thread_HipsDontLie = ForumThread.builder().author(shakira).title("Hips don't lie").creationDate(now()).build();
        thread_WakaWaka = ForumThread.builder().author(shakira).title("Waka Waka").creationDate(now().plusDays(1)).build();
        thread_Loca = ForumThread.builder().author(shakira).title("Loca").creationDate(now().plusDays(2)).build();
        forumThreadDao.saveAll(List.of(thread_HipsDontLie, thread_WakaWaka, thread_Loca));

    }

    @Test
    @DisplayName("Should return all threads, sorted by descending publication date")
    void getThreads() throws Exception {
        // when
        String jsonResult = mockMvc.perform(get("/threads"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        List<ForumThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ForumThreadResponse.class);
        assertThat(threadsResponses).hasSize(3);
        AssertableThread expectedThread_HipsDontLie = buildAssertableThread(thread_HipsDontLie);
        AssertableThread expectedThread_WakaWaka = buildAssertableThread(thread_WakaWaka);
        AssertableThread expectedThread_Loca = buildAssertableThread(thread_Loca);
        AssertableThread thread1 = buildAssertableThread(threadsResponses.get(0));
        AssertableThread thread2 = buildAssertableThread(threadsResponses.get(1));
        AssertableThread thread3 = buildAssertableThread(threadsResponses.get(2));
        assertThat(thread1).isEqualTo(expectedThread_Loca);
        assertThat(thread2).isEqualTo(expectedThread_WakaWaka);
        assertThat(thread3).isEqualTo(expectedThread_HipsDontLie);
    }

    @Test
    @DisplayName("Should return all threads when searching with empty criteria")
    void search_EmptyCriteria() throws Exception {
        // when
        String jsonResult = mockMvc.perform(
                        post("/threads/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(EMPTY_CRITERIA_WITHOUT_FILTER_AND_SORTING)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        List<ForumThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ForumThreadResponse.class);
        assertThat(threadsResponses).hasSize(3);
        AssertableThread expectedThread_HipsDontLie = buildAssertableThread(thread_HipsDontLie);
        AssertableThread expectedThread_WakaWaka = buildAssertableThread(thread_WakaWaka);
        AssertableThread expectedThread_Loca = buildAssertableThread(thread_Loca);
        AssertableThread thread1 = buildAssertableThread(threadsResponses.get(0));
        AssertableThread thread2 = buildAssertableThread(threadsResponses.get(1));
        AssertableThread thread3 = buildAssertableThread(threadsResponses.get(2));
        assertThat(thread1).isEqualTo(expectedThread_Loca);
        assertThat(thread2).isEqualTo(expectedThread_WakaWaka);
        assertThat(thread3).isEqualTo(expectedThread_HipsDontLie);
    }

    @Test
    @DisplayName("Should return threads matching given title")
    void search_SearchByTitleOK() throws Exception {
        // when
        ForumThreadSearch threadSearch = ForumThreadSearch.builder().criteria(ForumThreadSearch.Criteria.builder().title("waka").build()).build();
        String jsonResult = mockMvc.perform(
                        post("/threads/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(threadSearch)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        List<ForumThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ForumThreadResponse.class);
        assertThat(threadsResponses).hasSize(1);
        AssertableThread expectedThread_WakaWaka = buildAssertableThread(thread_WakaWaka);
        AssertableThread thread1 = buildAssertableThread(threadsResponses.get(0));
        assertThat(thread1).isEqualTo(expectedThread_WakaWaka);
    }

    @Test
    @DisplayName("Should return no threads when searching with title matching no threads")
    void search_SearchByTitleKO() throws Exception {
        // when
        ForumThreadSearch threadSearch = ForumThreadSearch.builder().criteria(ForumThreadSearch.Criteria.builder().title("test").build()).build();
        String jsonResult = mockMvc.perform(
                        post("/threads/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(threadSearch)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        List<ForumThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ForumThreadResponse.class);
        assertThat(threadsResponses).isEmpty();
    }

    @AfterAll
    void tearDown() {
        forumThreadDao.deleteAll();
        userDao.deleteAll();
    }

    private AssertableThread buildAssertableThread(ForumThread thread) {
        return AssertableThread.builder()
                .id(thread.getId())
                .author(AssertableUser.builder().id(thread.getAuthor().getId()).build())
                .title(thread.getTitle())
                .build();
    }

    private AssertableThread buildAssertableThread(ForumThreadResponse forumThreadResponse) {
        return AssertableThread.builder()
                .id(forumThreadResponse.getId())
                .author(AssertableUser.builder().id(forumThreadResponse.getAuthorId()).build())
                .title(forumThreadResponse.getTitle())
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
    private static class AssertableThread {
        Long id;
        AssertableUser author;
        String title;
        LocalDateTime creationDate;
    }
}
