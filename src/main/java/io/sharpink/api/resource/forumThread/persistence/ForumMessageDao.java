package io.sharpink.api.resource.forumThread.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumMessageDao extends PagingAndSortingRepository<ForumMessage, Long> {

    ForumMessage findFirstByOrderByPublicationDateDesc();
}
