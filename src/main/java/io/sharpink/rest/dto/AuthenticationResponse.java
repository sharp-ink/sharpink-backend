package io.sharpink.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
	
	private boolean authenticationSuccess;
	// utilisé si authenticationSuccess = false, null sinon
	private String errorMessage;

}
