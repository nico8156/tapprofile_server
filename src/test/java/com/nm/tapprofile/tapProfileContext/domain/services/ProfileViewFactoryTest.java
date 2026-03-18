package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProfileViewFactoryTest {

	@Test
	void should_create_profile_view_with_fixed_occurred_at() {
		var factory = new ProfileViewFactory(
				new FixedDateTimeProvider(Instant.parse("2026-03-17T18:00:00Z")));

		var profileId = new ProfileId(UUID.fromString("11111111-1111-1111-1111-111111111111"));

		var profileView = factory.create(profileId);

		assertNotNull(profileView.id());
		assertEquals(profileId, profileView.profileId());
		assertEquals(Instant.parse("2026-03-17T18:00:00Z"), profileView.occurredAt());
	}
}
