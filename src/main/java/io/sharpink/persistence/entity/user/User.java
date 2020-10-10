package io.sharpink.persistence.entity.user;

import java.util.List;
import java.util.Optional;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import io.sharpink.persistence.entity.story.Story;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  // Informations principales, obligatoires
  protected String nickname;
  protected String email;
  protected String password;

  // Informations complémentaires, optionnelles
  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @Getter(AccessLevel.NONE)
  protected UserDetails userDetails;

  // Les histoires de l'utilisateur

  /**
   * Permet d'avoir toujours l'information du nombre d'histoires d'un utilisateur, sans avoir à forcément récupérer les Story, qui seront <i>lazily loaded</i> par Hibernate
   */
  @Column(name = "STORIES_COUNT")
  protected Long storiesCount;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.TRUE)
  protected List<Story> stories;

  // getter personnalisé pour renvoyer un Optional<UserDetails> au lieu d'un UserDetails
  public Optional<UserDetails> getUserDetails() {
    return Optional.ofNullable(userDetails);
  }

}
