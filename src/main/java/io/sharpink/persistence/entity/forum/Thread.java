package io.sharpink.persistence.entity.forum;

import io.sharpink.persistence.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "FORUM_THREAD")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Thread {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "author_id")
  protected User originalAuthor;

  @Column(nullable = false)
  protected String title;

  @Column(name= "creation_date", nullable = false)
  protected LocalDateTime creationDate;

  @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.TRUE)
  protected List<Message> messages;
}
