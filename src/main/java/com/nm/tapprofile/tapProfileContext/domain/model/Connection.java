package com.nm.tapprofile.tapProfileContext.domain.model;

import java.time.Instant;
import java.util.Objects;

public final class Connection {

	private final ConnectionId id;
	private final ProfileId scannerProfileId;
	private final ProfileId scannedProfileId;
	private final Instant createdAt;

	public Connection(
			ConnectionId id,
			ProfileId scannerProfileId,
			ProfileId scannedProfileId,
			Instant createdAt) {
		this.id = Objects.requireNonNull(id);
		this.scannerProfileId = Objects.requireNonNull(scannerProfileId);
		this.scannedProfileId = Objects.requireNonNull(scannedProfileId);
		this.createdAt = Objects.requireNonNull(createdAt);
	}

	public ConnectionId id() {
		return id;
	}

	public ProfileId scannerProfileId() {
		return scannerProfileId;
	}

	public ProfileId scannedProfileId() {
		return scannedProfileId;
	}

	public Instant createdAt() {
		return createdAt;
	}
}
