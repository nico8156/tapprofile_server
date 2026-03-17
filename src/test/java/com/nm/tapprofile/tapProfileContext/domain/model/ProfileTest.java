package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileAlreadyPublishedError;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTest {

	@Test
	void should_publish_draft_profile() {
		var profile = new Profile(
				ProfileId.newId(),
				new Slug("alex-martin"),
				new DisplayName("Alex Martin"),
				new Headline("Backend developer"),
				new Bio("I build useful products."),
				ProfileStatus.DRAFT,
				Instant.parse("2026-03-17T10:00:00Z"),
				null);

		Instant publishedAt = Instant.parse("2026-03-17T11:00:00Z");

		var result = profile.publish(publishedAt);

		assertTrue(result.isSuccess());
		var publishedProfile = result.getSuccess();
		assertEquals(ProfileStatus.PUBLISHED, publishedProfile.status());
		assertEquals(publishedAt, publishedProfile.publishedAt());
	}

	@Test
	void should_fail_when_profile_is_already_published() {
		var profile = new Profile(
				ProfileId.newId(),
				new Slug("alex-martin"),
				new DisplayName("Alex Martin"),
				new Headline("Backend developer"),
				new Bio("I build useful products."),
				ProfileStatus.PUBLISHED,
				Instant.parse("2026-03-17T10:00:00Z"),
				Instant.parse("2026-03-17T11:00:00Z"));

		var result = profile.publish(Instant.parse("2026-03-17T12:00:00Z"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileAlreadyPublishedError);
	}
}
