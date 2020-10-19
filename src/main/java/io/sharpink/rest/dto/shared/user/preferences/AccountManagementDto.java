package io.sharpink.rest.dto.shared.user.preferences;

import io.sharpink.persistence.entity.user.preferences.StoriesDisplayMode;
import lombok.Data;

@Data
public class AccountManagementDto {
  private StoriesDisplayMode storiesDisplayMode;
}
