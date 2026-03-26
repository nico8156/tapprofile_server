package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicBadgeQuery;
import com.nm.tapprofile.tapProfileContext.domain.errors.BadgeNotFoundError;
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

import static org.junit.jupiter.api.Assertions.*;

class GetPublicBadgeQueryHandlerTest {

	@Test
	void should_return_public_badge_for_published_profile() {
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
				"alex-badge",
				"Alex Badge",
				"alex@example.com",
				"Backend developer",
				"I share my badge.")).getSuccess();

		var publishHandler = new PublishProfileCommandHandler(
				profileRepository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));
		publishHandler.handle(new PublishProfileCommand(profileId.value()));

		var badgeToken = badgeRepository.findByProfileId(profileId).orElseThrow().badgeToken();

		var handler = new GetPublicBadgeQueryHandler(badgeRepository, profileRepository);

		var result = handler.handle(new GetPublicBadgeQuery(badgeToken));

		assertTrue(result.isSuccess());
		assertEquals("alex-badge", result.getSuccess().slug());
		assertEquals("Alex Badge", result.getSuccess().displayName());
		assertEquals("VISITOR", result.getSuccess().role());
		assertEquals("ACTIVE", result.getSuccess().badge().status());
	}

	@Test
	void should_fail_when_badge_does_not_exist() {
		var handler = new GetPublicBadgeQueryHandler(
				new FakeBadgeRepository(),
				new FakeProfileRepository());

		var result = handler.handle(new GetPublicBadgeQuery("unknown-badge"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof BadgeNotFoundError);
	}
}
