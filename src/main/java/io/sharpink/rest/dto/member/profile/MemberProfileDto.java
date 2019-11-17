package io.sharpink.rest.dto.member.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileDto {
  private String nickname;
  private String email;
  private String firstName;
  private String lastName;
  private String profilePicture; // base64 profile picture
}
