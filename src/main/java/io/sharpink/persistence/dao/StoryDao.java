package io.sharpink.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.sharpink.persistence.entity.story.Story;

@Repository
public interface StoryDao extends CrudRepository<Story, Long> {

}
