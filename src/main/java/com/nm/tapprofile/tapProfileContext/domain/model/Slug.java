package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.FieldBlankError;
import com.nm.tapprofile.tapProfileContext.domain.errors.FieldTooLongError;
import com.nm.tapprofile.tapProfileContext.domain.errors.InvalidSlugError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public record Slug(String value) {

	private static final int MAX_LENGTH = 40;
	private static final int MIN_LENGTH = 3;
	private static final Pattern PATTERN = Pattern.compile("^[a-z0-9-]+$");

	public static Validation<ValidationError, Slug> create(String raw) {
		List<ValidationError> errors = new ArrayList<>();

		if (raw == null || raw.isBlank()) {
			errors.add(new FieldBlankError("slug"));
			return Validation.invalid(errors);
		}

		String normalized = raw.trim();

		if (normalized.length() < MIN_LENGTH) {
			errors.add(new InvalidSlugError("slug"));
		}

		if (normalized.length() > MAX_LENGTH) {
			errors.add(new FieldTooLongError("slug", MAX_LENGTH));
		}

		if (!PATTERN.matcher(normalized).matches()) {
			errors.add(new InvalidSlugError("slug"));
		}

		if (!errors.isEmpty()) {
			return Validation.invalid(errors);
		}

		return Validation.valid(new Slug(normalized));
	}
}
