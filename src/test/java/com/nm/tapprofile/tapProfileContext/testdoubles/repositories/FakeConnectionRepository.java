package com.nm.tapprofile.tapProfileContext.testdoubles.repositories;

import com.nm.tapprofile.tapProfileContext.application.ports.ConnectionRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.Connection;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;

import java.util.ArrayList;
import java.util.List;

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
}
