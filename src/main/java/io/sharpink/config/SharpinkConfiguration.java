package io.sharpink.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "sharpink")
@ConstructorBinding
@Getter
@AllArgsConstructor
public class SharpinkConfiguration {

    private final Resources resources;

    @Getter
    @AllArgsConstructor
    public static class Resources {
        private String webUrl;
        private String fileSystemPath;
    }

    public String getUsersProfilePictureWebUrl() {
        return this.getResources().getWebUrl() + "/users";
    }

    public String getUsersProfilePictureFileSystemPath() {
        return this.getResources().getFileSystemPath() + "/users";
    }
}
