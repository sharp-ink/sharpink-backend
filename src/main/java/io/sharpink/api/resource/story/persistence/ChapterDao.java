package io.sharpink.api.resource.story.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterDao extends PagingAndSortingRepository<Chapter, Long> {

}
