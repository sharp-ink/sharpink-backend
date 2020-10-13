package io.sharpink.rest.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPatchRequest {
  private String nickname;
  private String email;
  private String firstName;
  private String lastName;
  private String profilePicture; // base64 profile picture
}