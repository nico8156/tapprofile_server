package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileView;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileViewId;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;

public final class ProfileViewFactory {

	private final DateTimeProvider dateTimeProvider;

	public ProfileViewFactory(DateTimeProvider dateTimeProvider) {
		this.dateTimeProvider = dateTimeProvider;
	}

	public ProfileView create(ProfileId profileId) {
		return new ProfileView(
				ProfileViewId.newId(),
				profileId,
				dateTimeProvider.now());
	}
}
