package io.sharpink.rest.dto.member;

import lombok.Data;

/**
 * Représente les identifiants avec lesquels un Member s'inscrit ou par la suite essaie de se connecter.
 */
@Data
public class MemberCredentialsDto {
	
	// peut être le pseudo ou l'email
	private String login;
	// le mot de passe associé à ce compte
	private String password;
	

}
