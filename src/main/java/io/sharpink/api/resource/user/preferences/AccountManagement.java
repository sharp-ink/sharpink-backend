package io.sharpink.api.resource.user.preferences;

import io.sharpink.api.resource.user.persistence.user.preferences.StoriesDisplayMode;
import lombok.Data;

@Data
public class AccountManagement {
  private StoriesDisplayMode storiesDisplayMode;
}
