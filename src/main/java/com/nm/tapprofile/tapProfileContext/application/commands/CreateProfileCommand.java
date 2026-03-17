package com.nm.tapprofile.tapProfileContext.application.commands;

public record CreateProfileCommand(
		String slug,
		String displayName,
		String headline,
		String bio) {
}
