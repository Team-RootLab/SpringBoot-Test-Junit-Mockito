package com.rootlab.junit.test.dto;

import lombok.Data;

@Data
public class TwilioMessageRequest {
	private final String from;
	private final String to;
	private final String body;
}
