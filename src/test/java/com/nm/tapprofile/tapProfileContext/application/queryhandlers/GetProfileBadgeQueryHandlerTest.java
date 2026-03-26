package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetProfileBadgeQuery;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.MagicLinkFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkEmailSender;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetProfileBadgeQueryHandlerTest {

	@Test
	void should_return_profile_badge() {
		var profileRepository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				badgeRepository,
				new FakeMagicLinkRepository(),
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")), java.time.Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var profileId = createHandler.handle(new CreateProfileCommand(
				"alex-profile-badge",
				"Alex Profile Badge",
				"alex@example.com",
				"Backend developer",
				"I share my badge.")).getSuccess();

		var badgeToken = badgeRepository.findByProfileId(profileId).orElseThrow().badgeToken();
		var handler = new GetProfileBadgeQueryHandler(profileRepository, badgeRepository);

		var result = handler.handle(new GetProfileBadgeQuery(profileId.value()));

		assertTrue(result.isSuccess());
		assertEquals(badgeToken, result.getSuccess().badgeToken());
		assertEquals("http://localhost:3000/b/" + badgeToken, result.getSuccess().publicBadgeUrl());
	}

	@Test
	void should_fail_when_profile_does_not_exist() {
		var handler = new GetProfileBadgeQueryHandler(
				new FakeProfileRepository(),
				new FakeBadgeRepository());

		var result = handler.handle(new GetProfileBadgeQuery(UUID.randomUUID()));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotFoundError);
	}
}
