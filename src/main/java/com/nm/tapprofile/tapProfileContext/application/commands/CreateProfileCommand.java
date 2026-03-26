package com.nm.tapprofile.tapProfileContext.application.commands;

import com.nm.tapprofile.tapProfileContext.domain.model.ProfileRole;

public record CreateProfileCommand(
		String slug,
		String displayName,
		String email,
		String role,
		String headline,
		String bio) {
	public CreateProfileCommand(
			String slug,
			String displayName,
			String email,
			String headline,
			String bio) {
		this(slug, displayName, email, ProfileRole.VISITOR.name(), headline, bio);
	}
}
