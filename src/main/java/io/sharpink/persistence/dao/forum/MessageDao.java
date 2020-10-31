package io.sharpink.persistence.dao.forum;

import io.sharpink.persistence.entity.forum.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDao extends CrudRepository<Message, Long> {
}
