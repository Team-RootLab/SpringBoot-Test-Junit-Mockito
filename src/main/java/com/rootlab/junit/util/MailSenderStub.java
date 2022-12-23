package com.rootlab.junit.util;

import org.springframework.stereotype.Component;

// 가짜 객체
@Component
public class MailSenderStub implements MailSender{

	@Override
	public boolean send() {
		return true;
	}
}
