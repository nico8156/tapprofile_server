package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CaptureLeadCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.domain.errors.FieldBlankError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.LeadFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.MagicLinkFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeLeadRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkEmailSender;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CaptureLeadCommandHandlerTest {

	@Test
	void should_capture_lead_for_published_profile() {
		var profileRepository = new FakeProfileRepository();
		var leadRepository = new FakeLeadRepository();

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

		var handler = new CaptureLeadCommandHandler(
				profileRepository,
				leadRepository,
				new LeadFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CaptureLeadCommand(
				"alex-martin",
				"Nina",
				"nina@example.com",
				"On peut échanger après le meetup ?"));

		assertTrue(result.isSuccess());
		assertNotNull(result.getSuccess());

		var leads = leadRepository.findByProfileId(createResult.getSuccess());
		assertEquals(1, leads.size());
		assertEquals("Nina", leads.getFirst().firstName().value());
		assertEquals("nina@example.com", leads.getFirst().emailAddress().value());
		assertEquals(Instant.parse("2026-03-17T15:00:00Z"), leads.getFirst().createdAt());
	}

	@Test
	void should_fail_when_profile_does_not_exist() {
		var handler = new CaptureLeadCommandHandler(
				new FakeProfileRepository(),
				new FakeLeadRepository(),
				new LeadFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CaptureLeadCommand(
				"unknown-profile",
				"Nina",
				"nina@example.com",
				"Hello"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure().getFirst() instanceof ProfileNotFoundError);
	}

	@Test
	void should_fail_when_profile_is_not_published() {
		var profileRepository = new FakeProfileRepository();
		var leadRepository = new FakeLeadRepository();

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

		var handler = new CaptureLeadCommandHandler(
				profileRepository,
				leadRepository,
				new LeadFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CaptureLeadCommand(
				"alex-martin",
				"Nina",
				"nina@example.com",
				"Hello"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure().getFirst() instanceof ProfileNotPublishedError);
	}

	@Test
	void should_fail_with_accumulated_validation_errors() {
		var profileRepository = new FakeProfileRepository();
		var leadRepository = new FakeLeadRepository();

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

		var handler = new CaptureLeadCommandHandler(
				profileRepository,
				leadRepository,
				new LeadFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CaptureLeadCommand(
				"alex-martin",
				"",
				"not-an-email",
				"x".repeat(1001)));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure().size() >= 3);
		assertTrue(result.getFailure().stream().anyMatch(error -> error instanceof FieldBlankError));
	}
}
