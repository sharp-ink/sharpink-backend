package io.sharpink.persistence.entity.forum;

import io.sharpink.persistence.entity.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Comparator.naturalOrder;

@Entity
@Table(name = "FORUM_MESSAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message implements Comparable<Message> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne
  protected Thread thread;

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
  public int compareTo(Message o) {
    return this.publicationDate.compareTo(o.publicationDate);
  }
}
