
package io.sharpink.persistence.entity.story;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import io.sharpink.persistence.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "TITLE", columnDefinition = "Titre de l'histoire")
	private String title;

	@Column(name = "TYPE", columnDefinition = "Genre de l'histoire")
	@Enumerated(EnumType.STRING)
	private EnumStoryType type;

	@Column(name = "ORIGINAL_STORY", columnDefinition = "true = c'est une histoire originale, false = c'est une fan-fiction")
	private boolean originalStory;

	@Column(name = "STATUS", columnDefinition = "Statut actuel de l'histoire")
	@Enumerated(EnumType.STRING)
	private EnumStoryStatus status;
	
	@Column(name = "SUMMARY", columnDefinition = "Résumé de l'histoire")
	private String summary;

	@Column(name = "PUBLISHED", columnDefinition = "true = l'histoire est publiée et visible par tout le monde, false = seul l'auteur la voit")
	private boolean published;

	@Column(name = "CHAPTERS_NUMBER", columnDefinition = "Nombre de chapitres de l'histoire")
	private Long chaptersNumber;

	@ManyToOne
	private Member author;

	@OneToMany(mappedBy = "story")
	@LazyCollection(LazyCollectionOption.TRUE)
	private List<Chapter> chapters;

	@Column(name = "CREATION_DATE", columnDefinition = "Date de création de l'histoire (avec h,m,s)")
	private Date creationDate;

	@Column(name = "LAST_MODIFICATION_DATE", columnDefinition = "Date de dernière modification (publication de chapitres, modification du contenu d'un chapitre) (avec h,m,s)")
	private Date lastModificationDate;

	@Column(name = "FINAL_RELEASE_DATE", columnDefinition = "Date à laquelle l'histoire a officiellement été déclarée terminée par l'auteur (avec h,m,s)")
	private Date finalReleaseDate;

}
