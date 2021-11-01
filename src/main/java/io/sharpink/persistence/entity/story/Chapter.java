package io.sharpink.persistence.entity.story;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "STORY_CHAPTER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
