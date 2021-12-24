package io.sharpink.persistence.entity.forum;

import io.sharpink.persistence.entity.user.User;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Comparator.naturalOrder;

@Entity
@Table(name = "FORUM_THREAD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Thread implements Comparable<Thread> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    protected User author;

    @Column(nullable = false)
    protected String title;

    @Column(name = "creation_date", nullable = false)
    protected LocalDateTime creationDate;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.TRUE)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    protected List<Message> messages;

    public List<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    @Override
    public int compareTo(Thread o) {
        LocalDateTime threadLastPublicationDate = this.messages.isEmpty() ? this.creationDate : this.messages.get(this.messages.size() - 1).publicationDate;
        LocalDateTime otherThreadLastPublicationDate = o.messages.isEmpty() ? o.creationDate : o.messages.get(o.messages.size() - 1).publicationDate;
        return threadLastPublicationDate.compareTo(otherThreadLastPublicationDate);
    }

    public Message getLastPublishedMessage() {
        return getMessages().stream()
            .max(naturalOrder())
            .orElseThrow(() -> new IllegalArgumentException(format("Method 'getLastPublishedMessage()' called on an empty list for thread %d", id)));
    }
}
