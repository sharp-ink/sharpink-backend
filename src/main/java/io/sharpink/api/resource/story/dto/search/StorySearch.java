package io.sharpink.api.resource.story.dto.search;

import io.sharpink.api.shared.enums.SortType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorySearch {
    private Criteria criteria;
    private Filter filter;
    private Sort sort;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Criteria {
        private String title;
        private String authorName;
    }

    @Getter
    public static class Filter {
        // add fields here if filtering is needed in addition to search criteria
    }

    @Getter
    @Builder
    public static class Sort {
        private SortType title;
        private SortType authorName;
    }
}
