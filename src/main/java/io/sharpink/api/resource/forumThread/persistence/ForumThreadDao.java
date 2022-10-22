package io.sharpink.api.resource.forumThread.persistence;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumThreadDao extends PagingAndSortingRepository<ForumThread, Long>, JpaSpecificationExecutor<ForumThread> {

}
