package io.sharpink.api.resource.forumThread.persistence;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.*;

@Component
public class ForumThreadSpecification {

    public Specification<ForumThread> hasTitleLike(String title) {
        return isEmpty(title) ?
            (thread, cq, cb) -> cb.and() :
            (thread, cq, cb) -> cb.like(cb.lower(thread.get("title")), '%' + lowerCase(title) + '%');
    }

    public Specification<ForumThread> hasAuthorLike(String authorName) {
        return isEmpty(authorName) ?
            (thread, cq, cb) -> cb.and() :
            (thread, cq, cb) -> cb.like(cb.lower(thread.get("author").get("nickname")), '%' + lowerCase(authorName) + '%');
    }

    public Specification<ForumThread> threadContentContainsKeyWords(String keyWords) {
        return isEmpty(trim(keyWords)) ?
            (thread, cq, cb) -> cb.and() :
            (thread, cq, cb) -> {
                var keyWordsList = Arrays.asList(keyWords.split(" "));
                Join<ForumThread, ForumMessage> joinMessage = thread.join("messages");
                return cb.and(keyWordsList.stream()
                    .map(str -> cb.like(cb.lower(joinMessage.get("content")), '%' + lowerCase(str) + '%'))
                    .toArray(Predicate[]::new));
            };
    }

}
