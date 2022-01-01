package io.sharpink.api.resource.user.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDetailsResponse {

	protected String firstName;
	protected String lastName;
	protected String profilePicture;
	protected LocalDate birthDate;
	protected String country;
	protected String city;
	protected String job;
	protected boolean emailPublished;
	protected String customMessage;
	protected String biography;

}
