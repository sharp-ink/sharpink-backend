package io.sharpink.persistence.entity.story;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "STORY_CHAPTER")
@Data
@NoArgsConstructor
public class Chapter implements Comparable<Chapter> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @ToString.Exclude
  private Story story;

  @Column(name = "POSITION")
  private Integer position;

  @Column(name = "TITLE")
  private String title;

  @Column(name = "CONTENT")
  private String content;

  @Override
  public int compareTo(Chapter o) {
    return position.compareTo(o.position);
  }
}
