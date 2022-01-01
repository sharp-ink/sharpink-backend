package test.util;

import io.sharpink.api.resource.story.dto.StoryResponse;
import io.sharpink.api.resource.story.enums.StoryStatus;
import io.sharpink.api.resource.story.enums.StoryType;
import io.sharpink.api.resource.story.persistence.Chapter;
import io.sharpink.api.resource.story.persistence.Story;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

public class StoryMockUtil {

    static final Story STORY_MOCK = mockStory();
    static final StoryResponse STORY_RESPONSE_MOCK = mockStoryResponse();

    public static Story getStoryMock() {
        return STORY_MOCK;
    }

    public static StoryResponse getStoryResponseMock() {
        return STORY_RESPONSE_MOCK;
    }

    public static Story mockStory(long id, String title, int chaptersNumber) {
        List<Chapter> chapterListMock = new ArrayList<>();
        for (int i = 1; i <= chaptersNumber; i++) {
            chapterListMock.add(Chapter.builder()
                .title("Chapter " + i + " - " + RandomStringUtils.randomAlphabetic(10))
                .build());
        }

        return Story.builder()
            .id(id)
            .title(title)
            .chaptersNumber(chaptersNumber)
            .author(UserMockUtil.USER_MOCK)
            .chapters(chapterListMock)
            .build();
    }

    private static Story mockStory() {
        return Story.builder()
            .id(1L)
            .title("Hello world!")
            .type(StoryType.UNDETERMINED)
            .status(StoryStatus.PROGRESS)
            .chaptersNumber(0)
            .author(UserMockUtil.USER_MOCK)
            .chapters(emptyList())
            .build();
    }

    private static StoryResponse mockStoryResponse() {
        return null; // TODO
    }

}
