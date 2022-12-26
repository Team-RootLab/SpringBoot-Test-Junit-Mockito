package com.rootlab.junit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonResponseDto<T> {
	private final Integer code; // 1: 성공, 0: 실패
	private final String msg;
	private final T body;

	@Builder
	public CommonResponseDto(Integer code, String msg, T body) {
		this.code = code;
		this.msg = msg;
		this.body = body;
	}
}
