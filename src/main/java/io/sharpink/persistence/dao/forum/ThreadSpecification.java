package io.sharpink.persistence.dao.forum;

import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.persistence.entity.forum.Thread;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.StringUtils.lowerCase;

@Component
public class ThreadSpecification {

    public Specification<Thread> hasTitleLike(String title) {
        return isEmpty(title) ?
            (thread, cq, cb) -> cb.and() :
            (thread, cq, cb) -> cb.like(cb.lower(thread.get("title")), '%' + lowerCase(title) + '%');
    }

    public Specification<Thread> hasAuthorLike(String authorName) {
        return isEmpty(authorName) ?
            (thread, cq, cb) -> cb.and() :
            (thread, cq, cb) -> cb.like(cb.lower(thread.get("author").get("nickname")), '%' + lowerCase(authorName) + '%');
    }

    public Specification<Thread> threadContentContainsKeyWords(String keyWords) {
        return isEmpty(trim(keyWords)) ?
            (thread, cq, cb) -> cb.and() :
            (thread, cq, cb) -> {
                var keyWordsList = Arrays.asList(keyWords.split(" "));
                Join<Thread, Message> joinMessage = thread.join("messages");
                return cb.and(keyWordsList.stream()
                    .map(str -> cb.like(cb.lower(joinMessage.get("content")), '%' + lowerCase(str) + '%'))
                    .toArray(Predicate[]::new));
            };
    }

}
