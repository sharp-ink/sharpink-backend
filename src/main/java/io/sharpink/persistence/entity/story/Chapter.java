package io.sharpink.persistence.entity.story;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "STORY_CHAPTER")
@Data
@NoArgsConstructor
public class Chapter implements Comparable<Chapter> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
  @ToString.Exclude
	private Story story;

	@Column(name = "POSITION", columnDefinition = "Numéro du chapitre (commence à 1)")
	private int position;

	@Column(name = "TITLE", columnDefinition = "Titre du chapitre (optionnel)")
	private String title;

	@Column(name = "CONTENT", columnDefinition = "Le texte du chapitre, en une seule chaîne de caractères")
	private String content;

	@Override
	public int compareTo(Chapter o) {
		return ((Integer) position).compareTo(o.position);
	}

}
