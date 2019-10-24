package io.sharpink.rest.dto.member;

import java.util.List;

import io.sharpink.rest.dto.story.StoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

	protected Long id;

	// Informations principales, obligatoires

	protected String nickname;

	protected String email;

	protected Long storiesCount;

	protected List<StoryDto> stories;

	// Informations complémentaires, optionnelles

	protected MemberDetailsDto memberDetails;

	// public String getLocation() {
	//
	// String town = (this.memberDetails.town == null) ? "Ville non renseignée" :
	// StringUtils
	// .capitalize(this.memberDetails.town.toLowerCase());
	// String country = (this.memberDetails.country == null) ? "Pays non renseigné"
	// : memberDetails.country;
	// return town + " - " + country;
	//
	// }
	
}
