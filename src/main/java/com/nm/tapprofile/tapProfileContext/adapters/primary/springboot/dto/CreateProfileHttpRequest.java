package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

public record CreateProfileHttpRequest(
		String slug,
		String displayName,
		String role,
		String headline,
		String bio) {
	public CreateProfileHttpRequest(
			String slug,
			String displayName,
			String headline,
			String bio) {
		this(slug, displayName, null, headline, bio);
	}
}
