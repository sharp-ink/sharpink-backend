
package io.sharpink.api.resource.story.persistence;

import io.sharpink.api.resource.forumThread.persistence.ForumThread;
import io.sharpink.api.resource.user.persistence.user.User;
import io.sharpink.api.resource.story.enums.StoryStatus;
import io.sharpink.api.resource.story.enums.StoryType;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
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
    @Enumerated(EnumType.STRING)
    private StoryType type;

    @Column(name = "ORIGINAL_STORY")
    private boolean originalStory;

    @Column
    @Enumerated(EnumType.STRING)
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
    @JoinColumn(name = "THREAD_ID")
    private ForumThread thread;

    @Override
    public int compareTo(Story o) {
        return this.lastModificationDate.compareTo(o.lastModificationDate);
    }

    public boolean hasChapters() {
        return chaptersNumber >= 1;
    }
}
