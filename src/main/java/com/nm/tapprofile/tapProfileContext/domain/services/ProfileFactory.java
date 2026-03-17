package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.errors.ValidationError;
import com.nm.tapprofile.tapProfileContext.domain.model.*;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;
import com.nm.tapprofile.tapProfileContext.shared.validation.Validation;

public final class ProfileFactory {

	private final DateTimeProvider dateTimeProvider;

	public ProfileFactory(DateTimeProvider dateTimeProvider) {
		this.dateTimeProvider = dateTimeProvider;
	}

	public Validation<ValidationError, Profile> createDraft(
			String slug,
			String displayName,
			String headline,
			String bio) {
		return Validation.combine(
				Slug.create(slug),
				DisplayName.create(displayName),
				Headline.create(headline),
				Bio.create(bio),
				(validSlug, validDisplayName, validHeadline, validBio) -> new Profile(
						ProfileId.newId(),
						validSlug,
						validDisplayName,
						validHeadline,
						validBio,
						ProfileStatus.DRAFT,
						dateTimeProvider.now()));
	}
}
