package io.sharpink.persistence.entity.user;

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
  protected User user;

  protected String preferences;
}
