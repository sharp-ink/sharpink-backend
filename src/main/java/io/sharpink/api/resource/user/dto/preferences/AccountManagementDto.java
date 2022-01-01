package io.sharpink.api.resource.user.dto.preferences;

import io.sharpink.api.resource.user.persistence.user.preferences.StoriesDisplayMode;
import lombok.Data;

@Data
public class AccountManagementDto {
    private StoriesDisplayMode storiesDisplayMode;
}
