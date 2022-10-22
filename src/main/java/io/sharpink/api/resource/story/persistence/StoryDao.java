package io.sharpink.api.resource.story.persistence;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.lowerCase;

@Repository
public interface StoryDao extends PagingAndSortingRepository<Story, Long>, JpaSpecificationExecutor<Story> {

  List<Story> findByAuthorId(Long authorId);

  static Specification<Story> isPublic() {
    return (story, cq, cb) -> cb.isTrue(story.get("published"));
  }

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
