package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.domain.model.*;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

import java.util.ArrayList;
import java.util.List;

public final class ProfileFactory {

	private final DateTimeProvider dateTimeProvider;

	public ProfileFactory(DateTimeProvider dateTimeProvider) {
		this.dateTimeProvider = dateTimeProvider;
	}

	public Validation<ValidationError, Profile> createDraft(
			String slug,
			String displayName,
			String email,
			String role,
			String headline,
			String bio) {
		var slugValidation = Slug.create(slug);
		var displayNameValidation = DisplayName.create(displayName);
		var emailValidation = EmailAddress.create(email);
		var roleValidation = ProfileRole.create(role);
		var headlineValidation = Headline.create(headline);
		var bioValidation = Bio.create(bio);

		List<ValidationError> errors = new ArrayList<>();

		if (slugValidation.isInvalid()) {
			errors.addAll(slugValidation.getErrors());
		}

		if (displayNameValidation.isInvalid()) {
			errors.addAll(displayNameValidation.getErrors());
		}

		if (emailValidation.isInvalid()) {
			errors.addAll(emailValidation.getErrors());
		}

		if (roleValidation.isInvalid()) {
			errors.addAll(roleValidation.getErrors());
		}

		if (headlineValidation.isInvalid()) {
			errors.addAll(headlineValidation.getErrors());
		}

		if (bioValidation.isInvalid()) {
			errors.addAll(bioValidation.getErrors());
		}

		if (!errors.isEmpty()) {
			return Validation.invalid(errors);
		}

		return Validation.valid(new Profile(
				ProfileId.newId(),
				slugValidation.get(),
				displayNameValidation.get(),
				emailValidation.get(),
				roleValidation.get(),
				headlineValidation.get(),
				bioValidation.get(),
				ProfileStatus.DRAFT,
				dateTimeProvider.now(),
				null));
	}
}
