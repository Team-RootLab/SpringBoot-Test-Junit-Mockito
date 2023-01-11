package com.rootlab.junit.test.clients.twilio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix="twilio-api")
public class TwilioClientProperties {
	private String baseUrl;
	private String accountSid;
}
