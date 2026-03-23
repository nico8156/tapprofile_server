package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.InvalidProfileRoleError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

public enum ProfileRole {
	EXHIBITOR,
	VISITOR;

	public static Validation<ValidationError, ProfileRole> create(String raw) {
		if (raw == null || raw.isBlank()) {
			return Validation.invalid(java.util.List.of(new InvalidProfileRoleError("role")));
		}

		try {
			return Validation.valid(ProfileRole.valueOf(raw.trim().toUpperCase()));
		} catch (IllegalArgumentException exception) {
			return Validation.invalid(java.util.List.of(new InvalidProfileRoleError("role")));
		}
	}
}
