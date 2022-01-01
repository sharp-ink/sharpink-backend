package io.sharpink.api.resource.forumThread.persistence;

import io.sharpink.api.resource.user.persistence.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "FORUM_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumMessage implements Comparable<ForumMessage> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    protected ForumThread thread;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    protected User author;

    @Column(name = "date", nullable = false)
    protected LocalDateTime publicationDate;

    @Column(name = "number", nullable = false, unique = true, updatable = false)
    protected Integer number;

    @Column(nullable = false, columnDefinition = "TEXT")
    protected String content;

    @Override
    public int compareTo(ForumMessage o) {
        return this.publicationDate.compareTo(o.publicationDate);
    }
}
