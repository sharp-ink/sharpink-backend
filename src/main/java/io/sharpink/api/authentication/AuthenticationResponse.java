package io.sharpink.api.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

	private boolean authenticationSuccess;
	private String errorMessage; // used if authenticationSuccess = false, null otherwise

}
