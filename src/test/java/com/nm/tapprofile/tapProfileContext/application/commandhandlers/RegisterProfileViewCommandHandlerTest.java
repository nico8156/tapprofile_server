package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.RegisterProfileViewCommand;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.MagicLinkFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileViewFactory;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkEmailSender;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RegisterProfileViewCommandHandlerTest {

	@Test
	void should_register_view_for_published_profile() {
		var profileRepository = new FakeProfileRepository();
		var profileViewRepository = new FakeProfileViewRepository();

		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				new FakeBadgeRepository(),
				new FakeMagicLinkRepository(),
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")), java.time.Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var createResult = createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"alex@example.com",
				"Backend developer",
				"I build useful products."));

		var publishHandler = new PublishProfileCommandHandler(
				profileRepository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));

		publishHandler.handle(new PublishProfileCommand(createResult.getSuccess().value()));

		var handler = new RegisterProfileViewCommandHandler(
				profileRepository,
				profileViewRepository,
				new ProfileViewFactory(
						new FixedDateTimeProvider(Instant.parse("2026-03-17T18:00:00Z"))));

		var result = handler.handle(new RegisterProfileViewCommand("alex-martin"));

		assertTrue(result.isSuccess());
		assertNotNull(result.getSuccess());

		var views = profileViewRepository.findByProfileId(createResult.getSuccess());
		assertEquals(1, views.size());
		assertEquals(Instant.parse("2026-03-17T18:00:00Z"), views.get(0).occurredAt());
	}

	@Test
	void should_fail_when_profile_does_not_exist() {
		var handler = new RegisterProfileViewCommandHandler(
				new FakeProfileRepository(),
				new FakeProfileViewRepository(),
				new ProfileViewFactory(
						new FixedDateTimeProvider(Instant.parse("2026-03-17T18:00:00Z"))));

		var result = handler.handle(new RegisterProfileViewCommand("unknown-profile"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotFoundError);
	}

	@Test
	void should_fail_when_profile_is_not_published() {
		var profileRepository = new FakeProfileRepository();
		var profileViewRepository = new FakeProfileViewRepository();

		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				new FakeBadgeRepository(),
				new FakeMagicLinkRepository(),
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")), java.time.Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"alex@example.com",
				"Backend developer",
				"I build useful products."));

		var handler = new RegisterProfileViewCommandHandler(
				profileRepository,
				profileViewRepository,
				new ProfileViewFactory(
						new FixedDateTimeProvider(Instant.parse("2026-03-17T18:00:00Z"))));

		var result = handler.handle(new RegisterProfileViewCommand("alex-martin"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotPublishedError);
	}
}
