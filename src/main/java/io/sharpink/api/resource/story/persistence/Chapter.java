package io.sharpink.api.resource.story.persistence;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "STORY_CHAPTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chapter implements Comparable<Chapter> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
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
