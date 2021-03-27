package io.sharpink.rest.dto.response.user;

import io.sharpink.rest.dto.response.story.StoryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

  protected Long id;

  // Informations principales, obligatoires
  protected String nickname;
  protected String email;
  protected Long storiesCount;
  protected List<StoryResponse> stories;

  // Informations complémentaires, optionnelles
  protected UserDetailsResponse userDetails;

  // public String getLocation() {
  //
  // String town = (this.userDetails.town == null) ? "Ville non renseignée" :
  // StringUtils
  // .capitalize(this.userDetails.town.toLowerCase());
  // String country = (this.userDetails.country == null) ? "Pays non renseigné"
  // : userDetails.country;
  // return town + " - " + country;
  //
  // }

}
