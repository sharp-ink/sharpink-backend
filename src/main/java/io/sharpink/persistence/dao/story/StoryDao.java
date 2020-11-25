package io.sharpink.persistence.dao.story;

import io.sharpink.persistence.entity.story.Story;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;

@Repository
public interface StoryDao extends CrudRepository<Story, Long>, JpaSpecificationExecutor<Story> {

  List<Story> findByAuthorId(Long authorId);

  static Specification<Story> hasTitle(String title) {
    return (story, cq, cb) -> cb.equal(cb.trim(cb.lower(story.get("title"))), lowerCase(title));
  }

  static Specification<Story> hasTitleLike(String title) {
    return isEmpty(title) ?
      (story, cq, cb) -> cb.and() :
      (story, cq, cb) -> cb.like(cb.lower(story.get("title")), '%' + lowerCase(title) + '%');
  }

  static Specification<Story> hasAuthorLike(String authorName) {
    return isEmpty(authorName) ?
      (story, cq, cb) -> cb.and() :
      (story, cq, cb) -> cb.like(cb.lower(story.get("author").get("nickname")), '%' + lowerCase(authorName) + '%');
  }

}
