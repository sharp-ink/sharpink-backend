package io.sharpink.persistence.dao.forum;

import io.sharpink.persistence.entity.forum.Thread;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThreadDao extends CrudRepository<Thread, Long>, JpaSpecificationExecutor<Thread> {

}
