package com.nm.tapprofile.tapProfileContext.domain.services;

import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeadFactoryTest {

	@Test
	void should_create_lead_with_fixed_created_at() {
		var factory = new LeadFactory(
				new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z")));

		var result = factory.create(
				new ProfileId(UUID.fromString("11111111-1111-1111-1111-111111111111")),
				"Nina",
				"nina@example.com",
				"On peut échanger après l'événement ?");

		assertTrue(result.isValid());
		var lead = result.get();
		assertEquals("Nina", lead.firstName().value());
		assertEquals("nina@example.com", lead.emailAddress().value());
		assertEquals(Instant.parse("2026-03-17T15:00:00Z"), lead.createdAt());
	}

	@Test
	void should_accumulate_validation_errors() {
		var factory = new LeadFactory(
				new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z")));

		var result = factory.create(
				new ProfileId(UUID.fromString("11111111-1111-1111-1111-111111111111")),
				"",
				"not-an-email",
				"x".repeat(1001));

		assertTrue(result.isInvalid());
		assertTrue(result.getErrors().size() >= 3);
	}
}
