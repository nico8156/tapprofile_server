package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.model.Connection;
import com.nm.tapprofile.tapProfileContext.domain.model.ConnectionId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;

public final class ConnectionFactory {

	private final DateTimeProvider dateTimeProvider;

	public ConnectionFactory(DateTimeProvider dateTimeProvider) {
		this.dateTimeProvider = dateTimeProvider;
	}

	public Connection create(ProfileId scannerProfileId, ProfileId scannedProfileId) {
		return new Connection(
				ConnectionId.newId(),
				scannerProfileId,
				scannedProfileId,
				dateTimeProvider.now());
	}
}
