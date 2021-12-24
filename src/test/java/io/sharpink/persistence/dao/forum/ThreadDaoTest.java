package io.sharpink.persistence.dao.forum;

import io.sharpink.persistence.entity.forum.Message;
import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.persistence.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ThreadSpecification.class)
class ThreadDaoTest {

    @Autowired
    ThreadDao threadDao;

    @Autowired
    ThreadSpecification threadSpecification;

    @Autowired
    TestEntityManager entityManager;

    Thread thread1;
    Thread thread2;
    Thread thread3;
    Thread thread4;
    int allThreadsCount;

    @BeforeEach
    void init() {
        User arseneLupin = User.builder().nickname("gentleman-cambrioleur").build();
        User sherlockHolmes = User.builder().nickname("Sherlock_The_Door").build();
        List.of(arseneLupin, sherlockHolmes).forEach(entityManager::persist);

        thread1 = Thread.builder().title("Thief but gentleman").author(arseneLupin).creationDate(now()).build();
        thread2 = Thread.builder().title("To steal, stole, stolen").author(arseneLupin).creationDate(now()).build();
        thread3 = Thread.builder().title("Stole the sun").author(arseneLupin).creationDate(now()).build();
        thread4 = Thread.builder().title("List of best Baker Street bakeries").author(sherlockHolmes).creationDate(now()).build();
        var message = Message.builder().thread(thread1).author(arseneLupin).publicationDate(now()).number(1)
            .content("Hello, this is a comment by the famous Arsène Lupin").build();
        thread1.getMessages().add(message);
        var threads = List.of(thread1, thread2, thread3, thread4);
        allThreadsCount = threads.size();
        threads.forEach(entityManager::persist);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should return all threads when searching by title and passing null or empty value")
    void findAll_hasTitleLike_EmptyOrNullParameter() {
        // when
        List<Thread> threadsList1 = threadDao.findAll(threadSpecification.hasTitleLike(null));
        List<Thread> threadsList2 = threadDao.findAll(threadSpecification.hasTitleLike(""));

        // then
        assertThat(threadsList1).hasSize(allThreadsCount).containsExactlyInAnyOrder(thread1, thread2, thread3, thread4);
        assertThat(threadsList2).hasSize(allThreadsCount).containsExactlyInAnyOrder(thread1, thread2, thread3, thread4);
    }

    @Test
    @DisplayName("Should return only threads matching given title when searching by title")
    void findAll_hasTitleLike() {
        // when
        List<Thread> threads = threadDao.findAll(threadSpecification.hasTitleLike("stole"));

        // then
        assertThat(threads).hasSize(2).containsExactlyInAnyOrder(thread2, thread3);
    }

    @Test
    @DisplayName("Should return all threads when searching by author and passing null or empty value")
    void findAll_hasAuthorLike_EmptyOrNullParameter() {
        // when
        List<Thread> threadsList1 = threadDao.findAll(threadSpecification.hasAuthorLike(null));
        List<Thread> threadsList2 = threadDao.findAll(threadSpecification.hasAuthorLike(""));

        // then
        assertThat(threadsList1).hasSize(allThreadsCount).containsExactlyInAnyOrder(thread1, thread2, thread3, thread4);
        assertThat(threadsList2).hasSize(allThreadsCount).containsExactlyInAnyOrder(thread1, thread2, thread3, thread4);
    }

    @Test
    @DisplayName("Should return only threads matching given author when searching by author")
    void findAll_hasAuthorLike() {
        // when
        List<Thread> threads = threadDao.findAll(threadSpecification.hasAuthorLike("Sherlock"));

        // then
        assertThat(threads).hasSize(1).containsExactlyInAnyOrder(thread4);
    }

    @Test
    @DisplayName("Should return all threads when searching by content and passing null or empty value")
    void findAll_threadContentContainsKeyWords_EmptyOrNullParameter() {
        // when
        List<Thread> threadsList1 = threadDao.findAll(threadSpecification.threadContentContainsKeyWords(null));
        List<Thread> threadsList2 = threadDao.findAll(threadSpecification.threadContentContainsKeyWords(""));

        // then
        assertThat(threadsList1).hasSize(allThreadsCount).containsExactlyInAnyOrder(thread1, thread2, thread3, thread4);
        assertThat(threadsList2).hasSize(allThreadsCount).containsExactlyInAnyOrder(thread1, thread2, thread3, thread4);
    }

    @Test
    @DisplayName("Should return only threads having the content of one of their messages containing the key word " +
        "when searching by key words")
    void findAll_threadContentContainsKeyWords_UniqueKeyWord() {
        // when
        List<Thread> threads = threadDao.findAll(threadSpecification.threadContentContainsKeyWords("hello"));

        // then
        // only thread1, which has a message containing the word "Hello" should match
        assertThat(threads).hasSize(1).containsExactlyInAnyOrder(thread1);
    }

    @Test
    @DisplayName("Should return only threads having the content of one of their messages containing all the given key words " +
        "when searching by key words")
    void findAll_threadContentContainsKeyWords_MultipleKeyWords() {
        // when
        List<Thread> threads = threadDao.findAll(threadSpecification.threadContentContainsKeyWords("hello famous"));

        // then
        // only thread1, which has a message containing the words "Hello" AND "famous" should match
        assertThat(threads).hasSize(1).containsExactlyInAnyOrder(thread1);
    }

    @Test
    @DisplayName("Should return no threads when searching by multiple key words but no message contains al the given key words")
    void findAll_threadContentContainsKeyWords_NotFoundKeyWord() {
        // when
        List<Thread> threads = threadDao.findAll(threadSpecification.threadContentContainsKeyWords("hello fake_key_word"));

        // then
        // No message of any thread contains the words "hello" AND "fake_key_word"
        assertThat(threads).isEmpty();
    }
}
