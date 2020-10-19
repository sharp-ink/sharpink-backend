package io.sharpink.rest.dto.response.user.preferences;

import io.sharpink.persistence.entity.user.preferences.Theme;
import lombok.Data;

@Data
public class Appearance {
  private Theme theme;
  private AccountManagement accountManagement;
}
