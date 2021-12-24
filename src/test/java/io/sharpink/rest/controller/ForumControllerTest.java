package io.sharpink.rest.controller;

import io.sharpink.SharpInkBackendApplication;
import io.sharpink.persistence.dao.forum.ThreadDao;
import io.sharpink.persistence.dao.user.UserDao;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.rest.dto.request.forum.search.ThreadSearch;
import io.sharpink.rest.dto.response.forum.ThreadResponse;
import io.sharpink.rest.dto.response.story.StoryResponse;
import io.sharpink.util.json.JsonUtil;
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
class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ThreadDao threadDao;

    User shakira;
    Thread thread_HipsDontLie;
    Thread thread_WakaWaka;
    Thread thread_Loca;

    private final ThreadSearch EMPTY_CRITERIA_WITHOUT_FILTER_AND_SORTING = ThreadSearch.builder()
        .criteria(ThreadSearch.Criteria.builder()
            .title(null)
            .authorName(null)
            .keyWords(null).build())
        .filter(null)
        .sort(null).build();

    @BeforeAll
    void init() {
        shakira = User.builder().nickname("Shakira Isabel Mebarak Ripoll").build();
        userDao.save(shakira);

        thread_HipsDontLie = Thread.builder().author(shakira).title("Hips don't lie").creationDate(now()).build();
        thread_WakaWaka = Thread.builder().author(shakira).title("Waka Waka").creationDate(now().plusDays(1)).build();
        thread_Loca = Thread.builder().author(shakira).title("Loca").creationDate(now().plusDays(2)).build();
        threadDao.saveAll(List.of(thread_HipsDontLie, thread_WakaWaka, thread_Loca));

    }

    @Test
    @DisplayName("Should return all threads, sorted by descending publication date")
    void getThreads() throws Exception {
        // when
        String jsonResult = mockMvc.perform(get("/threads"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // then
        List<ThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ThreadResponse.class);
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
        List<ThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ThreadResponse.class);
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
        ThreadSearch threadSearch = ThreadSearch.builder().criteria(ThreadSearch.Criteria.builder().title("waka").build()).build();
        String jsonResult = mockMvc.perform(
                post("/threads/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(threadSearch)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // then
        List<ThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ThreadResponse.class);
        assertThat(threadsResponses).hasSize(1);
        AssertableThread expectedThread_WakaWaka = buildAssertableThread(thread_WakaWaka);
        AssertableThread thread1 = buildAssertableThread(threadsResponses.get(0));
        assertThat(thread1).isEqualTo(expectedThread_WakaWaka);
    }

    @Test
    @DisplayName("Should return no threads when searching with title matching no threads")
    void search_SearchByTitleKO() throws Exception {
        // when
        ThreadSearch threadSearch = ThreadSearch.builder().criteria(ThreadSearch.Criteria.builder().title("test").build()).build();
        String jsonResult = mockMvc.perform(
                post("/threads/search").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.toJson(threadSearch)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // then
        List<ThreadResponse> threadsResponses = JsonUtil.fromJsonArray(jsonResult, ThreadResponse.class);
        assertThat(threadsResponses).isEmpty();
    }

    @AfterAll
    void tearDown() {
        threadDao.deleteAll();
        userDao.deleteAll();
    }

    private AssertableThread buildAssertableThread(Thread thread) {
        return AssertableThread.builder()
            .id(thread.getId())
            .author(AssertableUser.builder().id(thread.getAuthor().getId()).build())
            .title(thread.getTitle())
            .build();
    }

    private AssertableThread buildAssertableThread(ThreadResponse threadResponse) {
        return AssertableThread.builder()
            .id(threadResponse.getId())
            .author(AssertableUser.builder().id(threadResponse.getAuthorId()).build())
            .title(threadResponse.getTitle())
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
