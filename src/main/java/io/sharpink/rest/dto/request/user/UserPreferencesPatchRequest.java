package io.sharpink.rest.dto.request.user;

import io.sharpink.persistence.entity.user.preferences.Theme;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesPatchRequest {
  private Theme theme;
}
