package io.sharpink.persistence.entity.user;

import io.sharpink.persistence.entity.user.preferences.Theme;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "USER_PREFERENCES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Integer id;

  @OneToOne
  @ToString.Exclude
  private User user;

  @Enumerated(EnumType.STRING)
  protected Theme theme;
}
