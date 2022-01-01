package io.sharpink.api.resource.user.persistence.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "USER_PREFERENCES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @OneToOne
  protected User user;

  @Column(nullable = false)
  protected String preferences; // JSON representation of the preferences
}
