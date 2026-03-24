package com.nm.tapprofile.tapProfileContext.testdoubles.repositories;

import com.nm.tapprofile.tapProfileContext.application.ports.ConnectionRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.Connection;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class FakeConnectionRepository implements ConnectionRepository {

	private final List<Connection> connections = new ArrayList<>();

	@Override
	public void save(Connection connection) {
		connections.add(connection);
	}

	@Override
	public List<Connection> findByProfileId(ProfileId profileId) {
		return connections.stream()
				.filter(connection -> connection.scannerProfileId().equals(profileId)
						|| connection.scannedProfileId().equals(profileId))
				.toList();
	}

	@Override
	public Optional<Connection> findByProfileIds(ProfileId firstProfileId, ProfileId secondProfileId) {
		return connections.stream()
				.filter(connection -> isSamePair(connection, firstProfileId, secondProfileId))
				.findFirst();
	}

	private boolean isSamePair(Connection connection, ProfileId firstProfileId, ProfileId secondProfileId) {
		return connection.scannerProfileId().equals(firstProfileId) && connection.scannedProfileId().equals(secondProfileId)
				|| connection.scannerProfileId().equals(secondProfileId) && connection.scannedProfileId().equals(firstProfileId);
	}
}
