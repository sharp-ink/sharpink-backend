package io.sharpink.api.resource.forumThread.dto.search;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumThreadSearch {
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
        private String keyWords;
    }

    @Getter
    public static class Filter {
        // add fields here if filtering is needed in addition to search criteria
    }

    @Getter
    @Builder
    public static class Sort {
        // add fields here if sorting is needed
    }
}
