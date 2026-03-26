package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateConnectionCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateConnectionCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.queries.GetMagicLinkQuery;
import com.nm.tapprofile.tapProfileContext.domain.errors.MagicLinkExpiredError;
import com.nm.tapprofile.tapProfileContext.domain.errors.MagicLinkNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.model.MagicLink;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ConnectionFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.MagicLinkFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeConnectionRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkEmailSender;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetMagicLinkQueryHandlerTest {

	@Test
	void should_return_profile_and_contacts_for_valid_magic_link() {
		var profileRepository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var magicLinkRepository = new FakeMagicLinkRepository();
		var connectionRepository = new FakeConnectionRepository();

		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				badgeRepository,
				magicLinkRepository,
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")), Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var ownerProfileId = createHandler.handle(new CreateProfileCommand(
				"alex-owner",
				"Alex Owner",
				"alex.owner@example.com",
				"Backend developer",
				"I own the dashboard.")).getSuccess();

		var contactProfileId = createHandler.handle(new CreateProfileCommand(
				"nina-contact",
				"Nina Contact",
				"nina.contact@example.com",
				"Product engineer",
				"I connect with people.")).getSuccess();

		var publishHandler = new PublishProfileCommandHandler(
				profileRepository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));
		publishHandler.handle(new PublishProfileCommand(contactProfileId.value()));

		var badgeToken = badgeRepository.findByProfileId(contactProfileId).orElseThrow().badgeToken();
		var connectionHandler = new CreateConnectionCommandHandler(
				profileRepository,
				badgeRepository,
				connectionRepository,
				new ConnectionFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T12:00:00Z"))));
		connectionHandler.handle(new CreateConnectionCommand(ownerProfileId.value(), badgeToken));

		var magicLink = magicLinkRepository.findByProfileId(ownerProfileId).orElseThrow();
		assertNull(magicLink.usedAt());

		var handler = new GetMagicLinkQueryHandler(
				magicLinkRepository,
				profileRepository,
				connectionRepository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T13:00:00Z")));

		var result = handler.handle(new GetMagicLinkQuery(magicLink.token()));

		assertTrue(result.isSuccess());
		assertEquals(ownerProfileId.value(), result.getSuccess().profile().profileId());
		assertEquals("alex.owner@example.com", result.getSuccess().profile().email());
		assertEquals(1, result.getSuccess().contacts().size());
		assertEquals("Nina Contact", result.getSuccess().contacts().getFirst().displayName());
		assertTrue(magicLinkRepository.findByToken(magicLink.token()).orElseThrow().usedAt() != null);
	}

	@Test
	void should_fail_when_magic_link_is_missing() {
		var handler = new GetMagicLinkQueryHandler(
				new FakeMagicLinkRepository(),
				new FakeProfileRepository(),
				new FakeConnectionRepository(),
				new FixedDateTimeProvider(Instant.parse("2026-03-17T13:00:00Z")));

		var result = handler.handle(new GetMagicLinkQuery(UUID.randomUUID()));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof MagicLinkNotFoundError);
	}

	@Test
	void should_fail_when_magic_link_is_expired() {
		var profileRepository = new FakeProfileRepository();
		var magicLinkRepository = new FakeMagicLinkRepository();
		var profileFactory = new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")));
		var profile = profileFactory.createDraft(
				"expired-owner",
				"Expired Owner",
				"expired.owner@example.com",
				"VISITOR",
				"Backend developer",
				"I own an expired link.").get();
		profileRepository.save(profile);

		var expiredLink = new MagicLink(
				UUID.randomUUID(),
				new ProfileId(profile.id().value()),
				Instant.parse("2026-03-17T11:00:00Z"),
				Instant.parse("2026-03-17T10:00:00Z"),
				null);
		magicLinkRepository.save(expiredLink);

		var handler = new GetMagicLinkQueryHandler(
				magicLinkRepository,
				profileRepository,
				new FakeConnectionRepository(),
				new FixedDateTimeProvider(Instant.parse("2026-03-17T12:00:00Z")));

		var result = handler.handle(new GetMagicLinkQuery(expiredLink.token()));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof MagicLinkExpiredError);
	}
}
