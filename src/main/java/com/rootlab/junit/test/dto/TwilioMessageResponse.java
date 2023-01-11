package com.rootlab.junit.test.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class TwilioMessageResponse {
	@JsonAlias("error_code")
	private String errorCode;
	@JsonAlias("error_message")
	private String errorMessage;
}
