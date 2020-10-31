package io.sharpink.persistence.dao.story;

import io.sharpink.persistence.entity.story.Chapter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterDao extends CrudRepository<Chapter, Long> {

}
