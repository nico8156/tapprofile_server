package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

public record CreateProfileHttpRequest(
		String slug,
		String displayName,
		String email,
		String role,
		String headline,
		String bio) {
	public CreateProfileHttpRequest(
			String slug,
			String displayName,
			String email,
			String headline,
			String bio) {
		this(slug, displayName, email, null, headline, bio);
	}
}
