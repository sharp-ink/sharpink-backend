package io.sharpink.api.resource.user.dto.preferences;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferencesDto {

    @Getter(AccessLevel.NONE)
    private AppearanceDto appearance;

    public AppearanceDto getAppearance() {
        if (appearance == null) {
            appearance = new AppearanceDto();
        }
        return appearance;
    }
}
