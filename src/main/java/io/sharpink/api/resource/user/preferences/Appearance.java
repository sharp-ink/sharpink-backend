package io.sharpink.api.resource.user.preferences;

import io.sharpink.api.resource.user.persistence.user.preferences.Theme;
import lombok.Data;

@Data
public class Appearance {
  private Theme theme;
  private AccountManagement accountManagement;
}
