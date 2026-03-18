package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

public record ErrorItemHttpResponse(
		String code,
		String message,
		String field) {
}
