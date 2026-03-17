package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.FieldBlankError;
import com.nm.tapprofile.tapProfileContext.domain.errors.FieldTooLongError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

import java.util.ArrayList;
import java.util.List;

public record Headline(String value) {

	private static final int MAX_LENGTH = 120;

	public static Validation<ValidationError, Headline> create(String raw) {
		List<ValidationError> errors = new ArrayList<>();

		if (raw == null || raw.isBlank()) {
			errors.add(new FieldBlankError("headline"));
			return Validation.invalid(errors);
		}

		String normalized = raw.trim();

		if (normalized.length() > MAX_LENGTH) {
			errors.add(new FieldTooLongError("headline", MAX_LENGTH));
		}

		if (!errors.isEmpty()) {
			return Validation.invalid(errors);
		}

		return Validation.valid(new Headline(normalized));
	}
}
