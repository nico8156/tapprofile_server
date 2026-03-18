package com.nm.tapprofile.tapProfileContext.application.commands;

public record CaptureLeadCommand(
		String slug,
		String firstName,
		String email,
		String message) {
}
