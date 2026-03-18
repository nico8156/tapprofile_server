package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

import java.util.List;

public record ErrorHttpResponse(
		List<ErrorItemHttpResponse> errors) {
}
