package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.model.ProfileStatus;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ProfileFactoryTest {

	@Test
	void should_create_draft_profile_with_fixed_created_at() {
		Instant fixedNow = Instant.parse("2026-03-17T10:15:30Z");
		var factory = new ProfileFactory(new FixedDateTimeProvider(fixedNow));

		var result = factory.createDraft(
				"alex-martin",
				"Alex Martin",
				"Backend developer",
				"I build useful products.");

		assertTrue(result.isValid());
		var profile = result.get();

		assertEquals(ProfileStatus.DRAFT, profile.status());
		assertEquals(fixedNow, profile.createdAt());
		assertEquals("alex-martin", profile.slug().value());
	}

	@Test
	void should_accumulate_multiple_validation_errors() {
		Instant fixedNow = Instant.parse("2026-03-17T10:15:30Z");
		var factory = new ProfileFactory(new FixedDateTimeProvider(fixedNow));

		var result = factory.createDraft(
				"A B",
				"",
				"",
				"x".repeat(501));

		assertTrue(result.isInvalid());
		assertTrue(result.getErrors().size() >= 4);
	}
}
