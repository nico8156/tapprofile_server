package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.domain.errors.FieldBlankError;
import com.nm.tapprofile.tapProfileContext.domain.errors.SlugAlreadyTakenError;
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

class CreateProfileCommandHandlerTest {

	@Test
	void should_create_profile_when_command_is_valid() {
		var repository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var magicLinkRepository = new FakeMagicLinkRepository();
		var magicLinkEmailSender = new FakeMagicLinkEmailSender();
		var factory = new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")));
		var badgeFactory = new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")));
		var magicLinkFactory = new MagicLinkFactory(
				new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")),
				java.time.Duration.ofDays(30));
		var handler = new CreateProfileCommandHandler(
				repository,
				badgeRepository,
				magicLinkRepository,
				magicLinkFactory,
				magicLinkEmailSender,
				factory,
				badgeFactory);

		var command = new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"alex@example.com",
				"Backend developer",
				"I build useful products.");

		var result = handler.handle(command);

		assertTrue(result.isSuccess());
		assertNotNull(result.getSuccess());

		var savedProfile = repository
				.findBySlug(new com.nm.tapprofile.tapProfileContext.domain.model.Slug("alex-martin"));
		assertTrue(savedProfile.isPresent());
		assertEquals("Alex Martin", savedProfile.get().displayName().value());
		assertEquals("alex@example.com", savedProfile.get().emailAddress().value());
		assertTrue(badgeRepository.findByProfileId(result.getSuccess()).isPresent());
		assertTrue(magicLinkRepository.findByProfileId(result.getSuccess()).isPresent());
		assertEquals(1, magicLinkEmailSender.sentEmails().size());
		assertEquals("alex@example.com", magicLinkEmailSender.sentEmails().getFirst().recipient());
	}

	@Test
	void should_fail_with_accumulated_validation_errors() {
		var repository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var magicLinkRepository = new FakeMagicLinkRepository();
		var factory = new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")));
		var badgeFactory = new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")));
		var handler = new CreateProfileCommandHandler(
				repository,
				badgeRepository,
				magicLinkRepository,
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")), java.time.Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				factory,
				badgeFactory);

		var command = new CreateProfileCommand(
				"",
				"",
				"",
				"",
				"x".repeat(501));

		var result = handler.handle(command);

		assertTrue(result.isFailure());
		assertTrue(result.getFailure().size() >= 4);
		assertTrue(result.getFailure().stream().anyMatch(error -> error instanceof FieldBlankError));
	}

	@Test
	void should_fail_when_slug_is_already_taken() {
		var repository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var magicLinkRepository = new FakeMagicLinkRepository();
		var factory = new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")));
		var badgeFactory = new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")));
		var handler = new CreateProfileCommandHandler(
				repository,
				badgeRepository,
				magicLinkRepository,
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:15:30Z")), java.time.Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				factory,
				badgeFactory);

		handler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"alex@example.com",
				"Backend developer",
				"First profile"));

		var secondCommand = new CreateProfileCommand(
				"alex-martin",
				"Another Alex",
				"another@example.com",
				"Java developer",
				"Second profile");

		var result = handler.handle(secondCommand);

		assertTrue(result.isFailure());
		assertEquals(1, result.getFailure().size());
		assertTrue(result.getFailure().getFirst() instanceof SlugAlreadyTakenError);
	}
}
