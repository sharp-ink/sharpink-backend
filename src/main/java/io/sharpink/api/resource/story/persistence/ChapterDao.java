package io.sharpink.api.resource.story.persistence;

import io.sharpink.api.resource.story.persistence.Chapter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterDao extends CrudRepository<Chapter, Long> {

}
