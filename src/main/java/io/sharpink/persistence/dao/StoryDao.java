package io.sharpink.persistence.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.sharpink.persistence.entity.story.Story;

import java.util.Optional;

@Repository
public interface StoryDao extends CrudRepository<Story, Long> {

  @Query("SELECT s FROM Story s WHERE trim(lower(s.title)) = lower(:title)")
  Optional<Story> findByTitle(@Param(value = "title") String title);
}
