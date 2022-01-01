package io.sharpink.api.resource.forumThread.persistence;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumThreadDao extends CrudRepository<ForumThread, Long>, JpaSpecificationExecutor<ForumThread> {

}
