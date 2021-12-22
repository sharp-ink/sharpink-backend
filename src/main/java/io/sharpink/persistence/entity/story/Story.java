
package io.sharpink.persistence.entity.story;

import io.sharpink.persistence.entity.forum.Thread;
import io.sharpink.persistence.entity.user.User;
import io.sharpink.shared.story.StoryStatus;
import io.sharpink.shared.story.StoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.EnumType.STRING;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Story implements Comparable<Story> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column
    @Enumerated(STRING)
    private StoryType type;

    @Column(name = "ORIGINAL_STORY")
    private boolean originalStory;

    @Column
    @Enumerated(STRING)
    private StoryStatus status;

    @Column
    private String summary;

    @Column
    private String thumbnail;

    @Column
    private boolean published;

    @Column(name = "CHAPTERS_NUMBER")
    private int chaptersNumber;

    @ManyToOne
    @ToString.Exclude
    private User author;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Chapter> chapters;

    @Column(name = "CREATION_DATE")
    private LocalDateTime creationDate;

    @Column(name = "LAST_MODIFICATION_DATE")
    private LocalDateTime lastModificationDate;

    @Column(name = "FINAL_RELEASE_DATE")
    private LocalDateTime finalReleaseDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thread_id")
    private Thread thread;

    @Override
    public int compareTo(Story o) {
        return this.lastModificationDate.compareTo(o.lastModificationDate);
    }
}
