package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.FieldBlankError;
import com.nm.tapprofile.tapProfileContext.domain.errors.FieldTooLongError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

import java.util.ArrayList;
import java.util.List;

public record FirstName(String value) {

	private static final int MAX_LENGTH = 80;

	public static Validation<ValidationError, FirstName> create(String raw) {
		List<ValidationError> errors = new ArrayList<>();

		if (raw == null || raw.isBlank()) {
			errors.add(new FieldBlankError("firstName"));
			return Validation.invalid(errors);
		}

		String normalized = raw.trim();

		if (normalized.length() > MAX_LENGTH) {
			errors.add(new FieldTooLongError("firstName", MAX_LENGTH));
		}

		if (!errors.isEmpty()) {
			return Validation.invalid(errors);
		}

		return Validation.valid(new FirstName(normalized));
	}
}
