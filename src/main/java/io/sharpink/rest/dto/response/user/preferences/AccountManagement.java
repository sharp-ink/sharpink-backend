package io.sharpink.rest.dto.response.user.preferences;

import io.sharpink.persistence.entity.user.preferences.StoriesDisplayMode;
import lombok.Data;

@Data
public class AccountManagement {
  private StoriesDisplayMode storiesDisplayMode;
}
