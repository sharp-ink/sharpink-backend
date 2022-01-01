package io.sharpink.api.resource.user.dto.preferences;

import io.sharpink.api.resource.user.persistence.user.preferences.Theme;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppearanceDto {
    private Theme theme;
    @Getter(AccessLevel.NONE)
    private AccountManagementDto accountManagement;

    public AccountManagementDto getAccountManagement() {
        if (accountManagement == null) {
            accountManagement = new AccountManagementDto();
        }
        return accountManagement;
    }
}
