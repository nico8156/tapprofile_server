package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.domain.model.*;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

public final class LeadFactory {

	private final DateTimeProvider dateTimeProvider;

	public LeadFactory(DateTimeProvider dateTimeProvider) {
		this.dateTimeProvider = dateTimeProvider;
	}

	public Validation<ValidationError, Lead> create(
			ProfileId profileId,
			String firstName,
			String email,
			String message) {
		return Validation.combine(
				FirstName.create(firstName),
				EmailAddress.create(email),
				LeadMessage.create(message),
				Validation.valid(profileId),
				(validFirstName, validEmail, validMessage, validProfileId) -> new Lead(
						LeadId.newId(),
						validProfileId,
						validFirstName,
						validEmail,
						validMessage,
						dateTimeProvider.now()));
	}
}
