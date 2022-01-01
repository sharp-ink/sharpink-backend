package io.sharpink.api.resource.user.persistence.user;

import io.sharpink.api.resource.story.persistence.Story;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "USER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    // Main information, mandatory
    protected String nickname;
    protected String email;
    protected String password;

    // Complementary information, optional
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    protected UserDetails userDetails;

    // User's preferences, optional
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter(AccessLevel.NONE)
    private UserPreferences userPreferences;

    // User's stories

    /**
     * Permet d'avoir toujours l'information du nombre d'histoires d'un utilisateur, sans avoir à forcément récupérer les Story, qui seront <i>lazily loaded</i> par Hibernate
     */
    @Column(name = "STORIES_COUNT")
    protected Long storiesCount;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.TRUE)
    protected List<Story> stories;

    // getter personnalisé pour renvoyer un Optional
    public Optional<UserDetails> getUserDetails() {
        return Optional.ofNullable(userDetails);
    }

    // getter personnalisé pour renvoyer un Optional
    public Optional<UserPreferences> getUserPreferences() {
        return Optional.ofNullable(userPreferences);
    }
}
