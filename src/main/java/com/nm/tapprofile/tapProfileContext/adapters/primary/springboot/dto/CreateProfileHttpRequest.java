package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

public record CreateProfileHttpRequest(
		String slug,
		String displayName,
		String headline,
		String bio) {
}
