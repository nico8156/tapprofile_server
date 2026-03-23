package com.nm.tapprofile.tapProfileContext.domain.errors;

public record InvalidProfileRoleError(String field) implements ValidationError {
	@Override
	public String code() {
		return "profile.role_invalid";
	}

	@Override
	public String message() {
		return "Field '%s' must be EXHIBITOR or VISITOR".formatted(field);
	}
}
