package io.sharpink.rest.dto.member;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MemberDetailsDto {

	protected String firstName;

	protected String lastName;

	protected LocalDate birthDate;

	protected String country;

	protected String city;

	protected String job;

	protected boolean emailPublished;

	protected String freeMessage;

	protected String biography;
	
}
