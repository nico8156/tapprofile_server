package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.FieldTooLongError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

import java.util.ArrayList;
import java.util.List;

public record Bio(String value) {

	private static final int MAX_LENGTH = 500;

	public static Validation<ValidationError, Bio> create(String raw) {
		String normalized = raw == null ? "" : raw.trim();
		List<ValidationError> errors = new ArrayList<>();

		if (normalized.length() > MAX_LENGTH) {
			errors.add(new FieldTooLongError("bio", MAX_LENGTH));
			return Validation.invalid(errors);
		}

		return Validation.valid(new Bio(normalized));
	}
}
