package io.sharpink.api.resource.forumThread.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumMessageDao extends CrudRepository<ForumMessage, Long> {
}
