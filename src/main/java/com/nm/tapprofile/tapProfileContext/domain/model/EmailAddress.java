package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.FieldBlankError;
import com.nm.tapprofile.tapProfileContext.domain.errors.FieldTooLongError;
import com.nm.tapprofile.tapProfileContext.domain.errors.InvalidEmailError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

	private static final int MAX_LENGTH = 255;
	private static final Pattern PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

	public static Validation<ValidationError, EmailAddress> create(String raw) {
		List<ValidationError> errors = new ArrayList<>();

		if (raw == null || raw.isBlank()) {
			errors.add(new FieldBlankError("email"));
			return Validation.invalid(errors);
		}

		String normalized = raw.trim();

		if (normalized.length() > MAX_LENGTH) {
			errors.add(new FieldTooLongError("email", MAX_LENGTH));
		}

		if (!PATTERN.matcher(normalized).matches()) {
			errors.add(new InvalidEmailError("email"));
		}

		if (!errors.isEmpty()) {
			return Validation.invalid(errors);
		}

		return Validation.valid(new EmailAddress(normalized));
	}
}
